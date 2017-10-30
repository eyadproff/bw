package sa.gov.nic.bio.bw.client.searchbyfaceimage;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.searchbyfaceimage.webservice.Candidate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SearchByFaceImagePaneFxController extends BodyFxControllerBase
{
	@FXML private SplitPane splitPane;
	@FXML private HBox imagePane;
	@FXML private ImageView ivCenterImage;
	@FXML private Button btnSelectImage;
	@FXML private Button btnSearchByImage;
	@FXML private ProgressIndicator piSearchByImage;
	@FXML private ScrollPane spCandidates;
	@FXML private TitledPane tpUploadedImage;
	@FXML private HBox hbCandidatesContainer;
	@FXML private HBox hbCandidatesImages;
	
	private FileChooser fileChooser = new FileChooser();
	private String uploadedImagePath;
	
	@FXML
	private void initialize()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Image file");
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
		FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
		fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
	}
	
	@Override
	public void onControllerReady()
	{
		imagePane.maxWidthProperty().bind(coreFxController.getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(coreFxController.getBodyPane().heightProperty());
		ivCenterImage.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivCenterImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(1.0));
		
		coreFxController.getPrimaryStage().maximizedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(!newValue) // on un-maximize (workaround to fix JavaFX bug)
			{
				Platform.runLater(() ->
				{
					imagePane.autosize();
					coreFxController.getBodyPane().autosize();
				});
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onReturnFromTask()
	{
		disableUiControls(false);
		
		Boolean successResponse = (Boolean) inputData.get("successResponse");
		if(successResponse != null && successResponse)
		{
			List<Candidate> candidates = (List<Candidate>) inputData.get("resultBean");
			
			spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(Double.MAX_VALUE));
			spCandidates.setManaged(true);
			spCandidates.setVisible(true);
			
			// make the list scrollable horizontally
			spCandidates.setOnScroll(event ->
			{
				if(event.getDeltaX() == 0 && event.getDeltaY() != 0)
				{
					spCandidates.setHvalue(spCandidates.getHvalue() - event.getDeltaY() * 3 / ((Pane) this.spCandidates.getContent()).getWidth());
				}
			});
			
			spCandidates.prefHeightProperty().bind(imagePane.heightProperty().divide(5));
			splitPane.getStyleClass().remove("hidden-divider"); // show the divider
			
			ImageView imageView = new ImageView();
			File imageFile = new File(uploadedImagePath);
			Image image = new Image(imageFile.toURI().toString());
			imageView.setImage(image);
			imageView.setPreserveRatio(true);
			final double[] hScrollbarHeight = {0.0};
			Optional<Node> optional = spCandidates.lookupAll(".scroll-bar").stream().filter(node -> ((ScrollBar) node).getOrientation() == Orientation.HORIZONTAL).findFirst();
			
			if(optional.isPresent())
			{
				ScrollBar scrollBar = ((ScrollBar) optional.get());
				hScrollbarHeight[0] = scrollBar.getHeight();
				/*scrollBar.visibleProperty().addListener((observable, oldValue, newValue) ->
                {
                    imageView.fitHeightProperty().bind(spCandidates.heightProperty().subtract(hScrollbarHeight[0] * (newValue ? 3 : 2)));
                });*/
				
				//imageView.fitHeightProperty().bind(spCandidates.heightProperty().subtract(hScrollbarHeight[0] * (scrollBar.isVisible() ? 3 : 2)));
				imageView.fitHeightProperty().bind(spCandidates.heightProperty().subtract(hScrollbarHeight[0] * 3));
			}
			
			tpUploadedImage.setContent(imageView);
			
			for(Candidate candidate : candidates)
			{
				ImageView candidateImageView = new ImageView();
				imageFile = new File(candidate.getPhotoPath());
				image = new Image(imageFile.toURI().toString());
				candidateImageView.setImage(image);
				candidateImageView.setPreserveRatio(true);
				candidateImageView.fitHeightProperty().bind(spCandidates.heightProperty().subtract(hScrollbarHeight[0] * 3));
				TitledPane titledPane = new TitledPane(String.valueOf(candidate.getScore()), candidateImageView);
				titledPane.setCollapsible(false);
				hbCandidatesImages.getChildren().add(titledPane);
			}
		}
		else super.onReturnFromTask();
	}
	
	@FXML
	private void onSelectImageClicked(ActionEvent actionEvent)
	{
		File selectedFile = fileChooser.showOpenDialog(coreFxController.getPrimaryStage());
		
		if(selectedFile != null)
		{
			String fileName = selectedFile.getName();
			String selectedFileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
			fileName = System.nanoTime() + "." + selectedFileExtension;
			uploadedImagePath = AppConstants.TEMP_FOLDER_PATH + "/" + fileName;
			
			try
			{
				Files.copy(selectedFile.toPath(), Paths.get(uploadedImagePath));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				BufferedImage bufferedImage = ImageIO.read(selectedFile);
				Image image = SwingFXUtils.toFXImage(bufferedImage, null);
				ivCenterImage.setImage(image);
				
				// success image loading
				
				/*imagePane.setMaxWidth(ivCenterImage.getImage().getWidth() * 2);
				imagePane.setMaxHeight(ivCenterImage.getImage().getHeight() * 2);*/
				btnSearchByImage.setDisable(false);
				btnSelectImage.setText(labelsBundle.getString("button.selectNewImage"));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void onSearchByImageClicked(ActionEvent actionEvent) throws IOException
	{
		// TODO: dialog
		/*String headerText = messagesBundle.getString("cancelCriminal.confirmation.header");
		String contentText = String.format(messagesBundle.getString("cancelCriminal.confirmation.message"), criminalId, personId);
		boolean confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);*/
		
		//if(!confirmed) return;
		
		hideNotification();
		disableUiControls(true);
		
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("uploadedImagePath", uploadedImagePath);
		
		coreFxController.submitFormTask(uiDataMap);
	}
	
	private void disableUiControls(boolean bool)
	{
		coreFxController.getMenuPaneController().showOverlayPane(bool);
		
		piSearchByImage.setVisible(bool);
		piSearchByImage.setManaged(bool);
		
		btnSelectImage.setManaged(!bool);
		btnSelectImage.setVisible(!bool);
		
		btnSearchByImage.setManaged(!bool);
		btnSearchByImage.setVisible(!bool);
	}
}