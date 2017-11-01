package sa.gov.nic.bio.bw.client.searchbyfaceimage;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.searchbyfaceimage.ui.ToggleTitledPane;
import sa.gov.nic.bio.bw.client.searchbyfaceimage.webservice.Candidate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class SearchByFaceImagePaneFxController extends BodyFxControllerBase
{
	@FXML private SplitPane splitPane;
	@FXML private HBox imagePane;
	@FXML private ImageView ivCenterImage;
	@FXML private VBox detailsPane;
	@FXML private Label lblBioId;
	@FXML private Label lblScore;
	@FXML private Label lblSamisId;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblFamilyName;
	@FXML private Button btnSelectImage;
	@FXML private Button btnSearchByImage;
	@FXML private Button btnCompareWithUploadedImage;
	@FXML private ProgressIndicator piSearchByImage;
	@FXML private ScrollPane spCandidates;
	@FXML private ToggleTitledPane tpUploadedImage;
	@FXML private HBox hbCandidatesContainer;
	@FXML private HBox hbCandidatesImages;
	
	private FileChooser fileChooser = new FileChooser();
	private String uploadedImagePath;
	private Image uploadedImage;
	
	@FXML
	private void initialize(){}
	
	@Override
	public void onControllerReady()
	{
		fileChooser.setTitle(labelsBundle.getString("fileChooser.selectImage.title"));
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter(labelsBundle.getString("fileChooser.selectImage.types"), "*.jpg");
		fileChooser.getExtensionFilters().addAll(extFilterJPG);
		
		imagePane.maxWidthProperty().bind(coreFxController.getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(coreFxController.getBodyPane().heightProperty());
		ivCenterImage.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivCenterImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(0.0));
		
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
			btnSearchByImage.setText(labelsBundle.getString("button.searchByImageAgain"));
			
			List<Candidate> candidates = (List<Candidate>) inputData.get("resultBean");
			Collections.sort(candidates);
			
			spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(Double.MAX_VALUE));
			spCandidates.setManaged(true);
			spCandidates.setVisible(true);
			btnCompareWithUploadedImage.setManaged(true);
			btnCompareWithUploadedImage.setVisible(true);
			btnCompareWithUploadedImage.setDisable(true);
			detailsPane.setManaged(true);
			detailsPane.setVisible(true);
			
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
			uploadedImage = new Image(imageFile.toURI().toString());
			imageView.setImage(uploadedImage);
			imageView.setPreserveRatio(true);
			final double[] hScrollbarHeight = {0.0};
			Optional<Node> optional = spCandidates.lookupAll(".scroll-bar").stream().filter(node -> ((ScrollBar) node).getOrientation() == Orientation.HORIZONTAL).findFirst();
			
			if(optional.isPresent())
			{
				ScrollBar scrollBar = ((ScrollBar) optional.get());
				hScrollbarHeight[0] = scrollBar.getHeight();
				imageView.fitHeightProperty().bind(spCandidates.heightProperty().subtract(hScrollbarHeight[0] * 3 + 2)); // 2 = top border + bottom border
			}
			
			tpUploadedImage.setContent(imageView);
			ToggleGroup toggleGroup = new ToggleGroup();
			tpUploadedImage.setToggleGroup(toggleGroup);
			toggleGroup.selectToggle(tpUploadedImage);
			tpUploadedImage.setOnMouseClicked(event ->
            {
                toggleGroup.selectToggle(tpUploadedImage);
                ivCenterImage.setImage(uploadedImage);
	            btnCompareWithUploadedImage.setDisable(true);
	            
	            lblBioId.setText(labelsBundle.getString("label.notAvailable"));
	            lblScore.setText(labelsBundle.getString("label.notAvailable"));
	            lblSamisId.setText(labelsBundle.getString("label.notAvailable"));
	            lblFirstName.setText(labelsBundle.getString("label.notAvailable"));
	            lblFatherName.setText(labelsBundle.getString("label.notAvailable"));
	            lblFamilyName.setText(labelsBundle.getString("label.notAvailable"));
            });
			
			hbCandidatesImages.getChildren().clear();
			
			for(Candidate candidate : candidates)
			{
				ImageView candidateImageView = new ImageView();
				imageFile = new File(candidate.getPhotoPath());
				Image candidateImage = new Image(imageFile.toURI().toString());
				candidateImageView.setImage(candidateImage);
				candidateImageView.setPreserveRatio(true);
				candidateImageView.fitHeightProperty().bind(spCandidates.heightProperty().subtract(hScrollbarHeight[0] * 3 + 2)); // 2 = top border + bottom border
				String scoreTitle = AppUtils.replaceNumbersOnly(String.valueOf(candidate.getScore()), Locale.getDefault());
				ToggleTitledPane toggleTitledPane = new ToggleTitledPane(scoreTitle, candidateImageView);
				toggleTitledPane.setToggleGroup(toggleGroup);
				toggleTitledPane.setCollapsible(false);
				toggleTitledPane.setOnMouseClicked(event ->
                {
                	toggleGroup.selectToggle(toggleTitledPane);
	                ivCenterImage.setImage(candidateImage);
	                btnCompareWithUploadedImage.setDisable(false);
	
	                lblBioId.setText(AppUtils.replaceNumbersOnly(String.valueOf(candidate.getBioId()), Locale.getDefault()));
	                lblScore.setText(AppUtils.replaceNumbersOnly(String.valueOf(candidate.getScore()), Locale.getDefault()));
	                
	                if(candidate.getSamisId() > 0)
	                {
		                lblSamisId.setText(AppUtils.replaceNumbersOnly(String.valueOf(candidate.getSamisId()), Locale.getDefault()));
		                lblFirstName.setText(candidate.getFirstName());
		                lblFatherName.setText(candidate.getFatherName());
		                lblFamilyName.setText(candidate.getFamilyName());
	                }
	                else
	                {
		                lblSamisId.setText(labelsBundle.getString("label.notAvailable"));
		                lblFirstName.setText(labelsBundle.getString("label.notAvailable"));
		                lblFatherName.setText(labelsBundle.getString("label.notAvailable"));
		                lblFamilyName.setText(labelsBundle.getString("label.notAvailable"));
	                }
                });
				hbCandidatesImages.getChildren().add(toggleTitledPane);
			}
		}
		else super.onReturnFromTask();
	}
	
	@FXML
	private void onSelectImageButtonClicked(ActionEvent actionEvent)
	{
		if(spCandidates.isVisible())
		{
			String headerText = messagesBundle.getString("selectNewFaceImage.confirmation.header");
			String contentText = messagesBundle.getString("selectNewFaceImage.confirmation.message");
			boolean confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
			
			if(!confirmed) return;
		}
		
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
				// TODO: check if bigger than 1 MB, and make it configurable
				
				btnSearchByImage.setDisable(false);
				btnSelectImage.setText(labelsBundle.getString("button.selectNewImage"));
				btnSearchByImage.setText(labelsBundle.getString("button.searchByImage"));
				btnSearchByImage.setManaged(true);
				btnSearchByImage.setVisible(true);
				spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(0.0));
				spCandidates.setManaged(false);
				spCandidates.setVisible(false);
				btnCompareWithUploadedImage.setManaged(false);
				btnCompareWithUploadedImage.setVisible(false);
				detailsPane.setManaged(false);
				detailsPane.setVisible(false);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void onSearchByImageButtonClicked(ActionEvent actionEvent) throws IOException
	{
		boolean confirmed;
		
		if(spCandidates.isVisible())
		{
			String headerText = messagesBundle.getString("searchByFaceImageWithExisting.confirmation.header");
			String contentText = messagesBundle.getString("searchByFaceImageWithExisting.confirmation.message");
			confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
		}
		else
		{
			String headerText = messagesBundle.getString("searchByFaceImage.confirmation.header");
			String contentText = messagesBundle.getString("searchByFaceImage.confirmation.message");
			confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
		}
		
		if(!confirmed) return;
		
		spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(0.0));
		spCandidates.setManaged(false);
		spCandidates.setVisible(false);
		
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
		
		btnCompareWithUploadedImage.setManaged(!bool);
		btnCompareWithUploadedImage.setVisible(!bool);
		
		detailsPane.setManaged(!bool);
		detailsPane.setVisible(!bool);
	}
	
	@FXML
	private void onCompareWithUploadedImageButtonClicked(ActionEvent actionEvent)
	{
		// get screen visual bounds
		Image selectedImage = ivCenterImage.getImage();
		
		if(uploadedImage.getHeight() >= selectedImage.getHeight())
		{
			double ratio = selectedImage.getHeight() / selectedImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = uploadedImage.getHeight() - selectedImage.getHeight();
			double extraWidth = heightDiff * ratio;
			selectedImage = scaleImage(selectedImage, selectedImage.getWidth() + extraWidth, selectedImage.getHeight() + heightDiff);
		}
		else
		{
			double ratio = uploadedImage.getHeight() / uploadedImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = selectedImage.getHeight() - uploadedImage.getHeight();
			double extraWidth = heightDiff * ratio;
			uploadedImage = scaleImage(uploadedImage, uploadedImage.getWidth() + extraWidth, uploadedImage.getHeight() + heightDiff);
		}
		
		String title = labelsBundle.getString("dialog.compare.title");
		String buttonText = labelsBundle.getString("dialog.compare.buttons.close");
		boolean rtl = coreFxController.getGuiState().getLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Image mergedImage;
		if(rtl) mergedImage = mergeImage(uploadedImage, selectedImage);
		else mergedImage = mergeImage(selectedImage, uploadedImage);
		
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeMenuItem = new MenuItem(buttonText);
		contextMenu.getItems().add(closeMenuItem);
		
		Button btnClose = new Button(buttonText);
		btnClose.setPadding(new Insets(10));
		StackPane stackPane = new StackPane();
		BorderPane borderPane = new BorderPane();
		StackPane.setAlignment(borderPane, Pos.CENTER);
		StackPane.setAlignment(btnClose, Pos.BOTTOM_CENTER);
		StackPane.setMargin(btnClose, new Insets(0, 0, 10, 0));
		stackPane.getChildren().addAll(borderPane, btnClose);
		ImageView ivMergedImage = new ImageView();
		ivMergedImage.setPreserveRatio(true);
		StackPane imageLayer = new StackPane();
		imageLayer.getChildren().add(ivMergedImage);
		borderPane.centerProperty().set(imageLayer);
		
		Stage dialogStage = DialogUtils.buildCustomDialog(appIcon, title, stackPane, buttonText, rtl);
		dialogStage.initOwner(coreFxController.getPrimaryStage());
		dialogStage.getScene().getRoot().setOnContextMenuRequested(event ->
        {
            contextMenu.show(dialogStage.getScene().getRoot(), event.getScreenX(), event.getScreenY());
        });
		
		closeMenuItem.setOnAction(event -> dialogStage.close());
		
		ivMergedImage.fitHeightProperty().bind(dialogStage.heightProperty());
		ivMergedImage.fitWidthProperty().bind(dialogStage.widthProperty());
		
		ivMergedImage.setImage(mergedImage);
		btnClose.setOnAction(event -> dialogStage.close());
		
		dialogStage.setFullScreenExitHint("");
		dialogStage.setFullScreen(true);
		dialogStage.show();
	}
	
	private static Image scaleImage(Image source, double targetWidth, double targetHeight)
	{
		ImageView imageView = new ImageView(source);
		imageView.setPreserveRatio(true);
		imageView.setFitWidth(targetWidth);
		imageView.setFitHeight(targetHeight);
		return imageView.snapshot(null, null);
	}
	
	private static Image mergeImage(Image right, Image left)
	{
		//do some calculate first
		int offset  = 5;
		double width = left.getWidth() + right.getWidth() + offset;
		double height = Math.max(left.getHeight(),right.getHeight()) + offset;
		//create a new buffer and draw two image into the new image
		BufferedImage newImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		Color oldColor = g2.getColor();
		//fill background
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, (int) width, (int) height);
		//draw image
		g2.setColor(oldColor);
		g2.drawImage(SwingFXUtils.fromFXImage(left, null), null, 0, 0);
		g2.drawImage(SwingFXUtils.fromFXImage(right, null), null, (int) left.getWidth() + offset, 0);
		g2.dispose();
		
		return SwingFXUtils.toFXImage(newImage, null);
	}
}