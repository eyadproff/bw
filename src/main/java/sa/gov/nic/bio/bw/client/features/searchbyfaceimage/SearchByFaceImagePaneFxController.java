package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ui.ToggleTitledPane;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickable(btnSelectImage);
		GuiUtils.makeButtonClickable(btnSearchByImage);
		GuiUtils.makeButtonClickable(btnCompareWithUploadedImage);
	}
	
	@Override
	public void onControllerReady()
	{
		fileChooser.setTitle(stringsBundle.getString("fileChooser.selectImage.title"));
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter(stringsBundle.getString("fileChooser.selectImage.types"), "*.jpg");
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
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(!newForm)
		{
			ServiceResponse<?> serviceResponse = (ServiceResponse<?>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			// hide the overlay on top of the side menus
			coreFxController.getMenuPaneController().showOverlayPane(false);
			
			// hide the progress bar
			GuiUtils.showNode(piSearchByImage, false);
			
			// show the the first two buttons
			GuiUtils.showNode(btnSelectImage, true);
			GuiUtils.showNode(btnSearchByImage, true);
			
			if(serviceResponse.isSuccess())
			{
				btnSearchByImage.setText(stringsBundle.getString("button.searchByImageAgain"));
				
				List<Candidate> candidates = (List<Candidate>) serviceResponse.getResult();
				Collections.sort(candidates);
				
				spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(Double.MAX_VALUE));
				btnCompareWithUploadedImage.setDisable(true);
				
				GuiUtils.showNode(spCandidates, true);
				GuiUtils.showNode(btnCompareWithUploadedImage, true);
				GuiUtils.showNode(detailsPane, true);
				
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
				
				    lblBioId.setText(stringsBundle.getString("label.notAvailable"));
				    lblScore.setText(stringsBundle.getString("label.notAvailable"));
				    lblSamisId.setText(stringsBundle.getString("label.notAvailable"));
				    lblFirstName.setText(stringsBundle.getString("label.notAvailable"));
				    lblFatherName.setText(stringsBundle.getString("label.notAvailable"));
				    lblFamilyName.setText(stringsBundle.getString("label.notAvailable"));
				});
				
				hbCandidatesImages.getChildren().clear();
				
				for(Candidate candidate : candidates)
				{
					ImageView candidateImageView = new ImageView();
					byte[] photoByteArray = Base64.getDecoder().decode(candidate.getImage());
					Image candidateImage = new Image(new ByteArrayInputStream(photoByteArray));
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
					    lblBioId.setText(stringsBundle.getString("label.notAvailable"));
					    lblScore.setText(stringsBundle.getString("label.notAvailable"));
					    lblSamisId.setText(stringsBundle.getString("label.notAvailable"));
					    lblFirstName.setText(stringsBundle.getString("label.notAvailable"));
					    lblFatherName.setText(stringsBundle.getString("label.notAvailable"));
					    lblFamilyName.setText(stringsBundle.getString("label.notAvailable"));
					
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
				
				GuiUtils.showNode(spCandidates, false);
				GuiUtils.showNode(btnCompareWithUploadedImage, false);
				GuiUtils.showNode(detailsPane, false);
				
				reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                       serviceResponse.getErrorDetails());
			}
		}
	}
	
	@FXML
	private void onSelectImageButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
		if(spCandidates.isVisible())
		{
			String headerText = stringsBundle.getString("selectNewFaceImage.confirmation.header");
			String contentText = stringsBundle.getString("selectNewFaceImage.confirmation.message");
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
							showWarningNotification(String.format(stringsBundle.getString("selectNewFaceImage.fileChooser.exceedMaxFileSize"), df.format(fileSizeKB), df.format(maxFileSizeKb)));
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
				btnSelectImage.setText(stringsBundle.getString("button.selectNewImage"));
				btnSearchByImage.setText(stringsBundle.getString("button.searchByImage"));
				spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(0.0));
				
				GuiUtils.showNode(btnSearchByImage, true);
				GuiUtils.showNode(spCandidates, false);
				GuiUtils.showNode(btnCompareWithUploadedImage, false);
				GuiUtils.showNode(detailsPane, false);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void onSearchByImageButtonClicked(ActionEvent actionEvent)
	{
		boolean confirmed;
		
		if(spCandidates.isVisible())
		{
			String headerText = stringsBundle.getString("searchByFaceImageWithExisting.confirmation.header");
			String contentText = stringsBundle.getString("searchByFaceImageWithExisting.confirmation.message");
			confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
		}
		else
		{
			String headerText = stringsBundle.getString("searchByFaceImage.confirmation.header");
			String contentText = stringsBundle.getString("searchByFaceImage.confirmation.message");
			confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
		}
		
		if(!confirmed) return;
		
		spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(0.0));
		GuiUtils.showNode(spCandidates, false);
		
		hideNotification();
		
		// show the overlay on top of the side menus
		coreFxController.getMenuPaneController().showOverlayPane(true);
		
		// show the progress bar
		GuiUtils.showNode(piSearchByImage, true);
		
		// hide the buttons
		GuiUtils.showNode(btnSelectImage, false);
		GuiUtils.showNode(btnSearchByImage, false);
		GuiUtils.showNode(btnCompareWithUploadedImage, false);
		
		// hide the details pane
		GuiUtils.showNode(detailsPane, false);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("uploadedImagePath", uploadedImagePath);
		
		coreFxController.submitForm(uiDataMap);
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
		
		String title = stringsBundle.getString("dialog.compare.title");
		String buttonText = stringsBundle.getString("dialog.compare.buttons.close");
		boolean rtl = coreFxController.getCurrentLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
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
		
		Stage dialogStage = DialogUtils.buildCustomDialog(coreFxController.getPrimaryStage(), title, stackPane, rtl);
		dialogStage.initStyle(StageStyle.UNDECORATED);
		dialogStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, t ->
		{
			if(t.getCode() == KeyCode.ESCAPE)
			{
				contextMenu.hide();
				dialogStage.close();
			}
		});
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
		dialogStage.setOnHidden(event -> coreFxController.unregisterStageForIdleMonitoring(dialogStage));
		coreFxController.registerStageForIdleMonitoring(dialogStage);
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