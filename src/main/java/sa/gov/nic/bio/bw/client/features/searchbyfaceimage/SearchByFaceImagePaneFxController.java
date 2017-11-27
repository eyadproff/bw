package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ui.ToggleTitledPane;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchByFaceImagePaneFxController extends BodyFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(SearchByFaceImagePaneFxController.class.getName());
	
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
		// hide the overlay on top of the side menus
		coreFxController.getMenuPaneController().showOverlayPane(false);
		
		// hide the progress bar
		piSearchByImage.setVisible(false);
		piSearchByImage.setManaged(false);
		
		// show the the first two buttons
		btnSelectImage.setManaged(true);
		btnSelectImage.setVisible(true);
		btnSearchByImage.setManaged(true);
		btnSearchByImage.setVisible(true);
		
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
	                
	                // default values
	                lblBioId.setText(labelsBundle.getString("label.notAvailable"));
	                lblScore.setText(labelsBundle.getString("label.notAvailable"));
	                lblSamisId.setText(labelsBundle.getString("label.notAvailable"));
	                lblFirstName.setText(labelsBundle.getString("label.notAvailable"));
	                lblFatherName.setText(labelsBundle.getString("label.notAvailable"));
	                lblFamilyName.setText(labelsBundle.getString("label.notAvailable"));
	
	                long bioId = candidate.getBioId();
	                int score = candidate.getScore();
	                long samisId = candidate.getSamisId();
	                String firstName = candidate.getFirstName();
	                String fatherName = candidate.getFatherName();
	                String familyName = candidate.getFamilyName();
	
	                if(firstName != null && firstName.trim().isEmpty()) firstName = null;
	                if(fatherName != null && fatherName.trim().isEmpty()) fatherName = null;
	                if(familyName != null && familyName.trim().isEmpty()) familyName = null;
	                
	                String sBioId = AppUtils.replaceNumbersOnly(String.valueOf(bioId), Locale.getDefault());
	                String sScore = AppUtils.replaceNumbersOnly(String.valueOf(score), Locale.getDefault());
	
	                lblBioId.setText(sBioId);
	                lblScore.setText(sScore);
	                
	                if(samisId > 0)
	                {
		                String sSamisId = AppUtils.replaceNumbersOnly(String.valueOf(samisId), Locale.getDefault());
		                lblSamisId.setText(sSamisId);
	                }
	                
	                if(firstName != null) lblFirstName.setText(firstName);
	                if(fatherName != null) lblFatherName.setText(fatherName);
	                if(familyName != null) lblFamilyName.setText(familyName);
                });
				hbCandidatesImages.getChildren().add(toggleTitledPane);
			}
			
			spCandidates.setHvalue(0.0); // scroll to the beginning
		}
		else // on failure response
		{
			spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(0.0));
			spCandidates.setManaged(false);
			spCandidates.setVisible(false);
			btnCompareWithUploadedImage.setManaged(false);
			btnCompareWithUploadedImage.setVisible(false);
			detailsPane.setManaged(false);
			detailsPane.setVisible(false);
			
			super.onReturnFromTask(); // let the parent class show the error message
		}
	}
	
	@FXML
	private void onSelectImageButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
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
			try
			{
				long fileSizeBytes = Files.size(selectedFile.toPath());
				double fileSizeKB = fileSizeBytes / 1024.0;
				String maxFileSizeKbProperty = System.getProperty("jnlp.bio.bw.config.searchByFaceImage.fileMaxSizeKB");
				if(maxFileSizeKbProperty == null)
				{
					LOGGER.warning("jnlp.bw.config.searchByFaceImage.fileMaxSizeKB is null!");
				}
				else
				{
					try
					{
						double maxFileSizeKb = Double.parseDouble(maxFileSizeKbProperty);
						if(fileSizeKB > maxFileSizeKb)
						{
							DecimalFormat df = new DecimalFormat("#.00"); // 2 decimal places
							showWarningNotification(String.format(messagesBundle.getString("selectNewFaceImage.fileChooser.exceedMaxFileSize"), df.format(fileSizeKB), df.format(maxFileSizeKb)));
							return;
						}
					}
					catch(NumberFormatException e)
					{
						LOGGER.log(Level.WARNING, "Failed to parse jnlp.bw.config.searchByFaceImage.fileMaxSizeKB (" + maxFileSizeKbProperty + ") as double!", e);
					}
				}
			}
			catch(IOException e)
			{
				LOGGER.log(Level.WARNING, "Failed to retrieve the file size (" + selectedFile.getAbsolutePath() + ")!", e);
			}
			
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
		
		// show the overlay on top of the side menus
		coreFxController.getMenuPaneController().showOverlayPane(true);
		
		// show the progress bar
		piSearchByImage.setVisible(true);
		piSearchByImage.setManaged(true);
		
		// hide the buttons
		btnSelectImage.setManaged(false);
		btnSelectImage.setVisible(false);
		btnSearchByImage.setManaged(false);
		btnSearchByImage.setVisible(false);
		btnCompareWithUploadedImage.setManaged(false);
		btnCompareWithUploadedImage.setVisible(false);
		
		// hide the details pane
		detailsPane.setManaged(false);
		detailsPane.setVisible(false);
		
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("uploadedImagePath", uploadedImagePath);
		
		coreFxController.submitFormTask(uiDataMap);
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
		dialogStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, t ->
		{
			if(t.getCode() == KeyCode.ESCAPE)
			{
				contextMenu.hide();
				dialogStage.close();
			}
		});
		dialogStage.initOwner(coreFxController.getPrimaryStage());
		dialogStage.getScene().getRoot().setOnContextMenuRequested(event -> contextMenu.show(dialogStage.getScene().getRoot(), event.getScreenX(), event.getScreenY()));
		
		closeMenuItem.setOnAction(event -> dialogStage.close());
		
		ivMergedImage.fitHeightProperty().bind(dialogStage.heightProperty());
		ivMergedImage.fitWidthProperty().bind(dialogStage.widthProperty());
		
		ivMergedImage.setImage(mergedImage);
		btnClose.setOnAction(event ->
		{
			dialogStage.close();
			contextMenu.hide();
		});
		
		dialogStage.setFullScreenExitHint("");
		dialogStage.setFullScreen(true);
		dialogStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
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