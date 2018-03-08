package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ui.ToggleTitledPane;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow.SearchByFaceImageWorkflow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ShowResultFxController extends WizardStepFxControllerBase
{
	@FXML private ResourceBundle resources;
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
	@FXML private Button btnCompareWithUploadedImage;
	@FXML private ScrollPane spCandidates;
	@FXML private ToggleTitledPane tpFinalImage;
	@FXML private HBox hbCandidatesContainer;
	@FXML private HBox hbCandidatesImages;
	@FXML private Button btnStartOver;
	
	private Image finalImage;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/showResults.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	public void onControllerReady()
	{
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
	
	@Override
	@SuppressWarnings("unchecked")
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		List<Candidate> candidates = (List<Candidate>) uiInputData.get(SearchByFaceImageWorkflow.KEY_CANDIDATES);
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
		        spCandidates.setHvalue(spCandidates.getHvalue() - event.getDeltaY() * 3 /
				                                                ((Pane) this.spCandidates.getContent()).getWidth());
		    }
		});
		
		spCandidates.prefHeightProperty().bind(imagePane.heightProperty().divide(5));
		splitPane.getStyleClass().remove("hidden-divider"); // show the divider
		
		ImageView imageView = new ImageView();
		finalImage = (Image) uiInputData.get(SearchByFaceImageWorkflow.KEY_FINAL_IMAGE);
		imageView.setImage(finalImage);
		imageView.setPreserveRatio(true);
		
		final double[] hScrollbarHeight = {13.0};
		imageView.fitHeightProperty().bind(spCandidates.heightProperty()
                                   .subtract(hScrollbarHeight[0] * 3 + 2)); // 2 = top border + bottom border
		GuiUtils.attachImageDialog(coreFxController, imageView, tpFinalImage.getText(),
		                           resources.getString("label.contextMenu.showImage"));
		
		imagePane.autosize();
		
		// workaround to resolve the issue of not resizing sometimes
		new Thread(() ->
		{
		    try
		    {
		        Thread.sleep(1);
		    }
		    catch(InterruptedException e){e.printStackTrace();}
		    Platform.runLater(() -> imagePane.autosize());
		}).start();
		
		ivCenterImage.setImage(finalImage);
		GuiUtils.attachImageDialog(coreFxController, ivCenterImage, tpFinalImage.getText(),
		                           resources.getString("label.contextMenu.showImage"));
		
		tpFinalImage.setContent(imageView);
		ToggleGroup toggleGroup = new ToggleGroup();
		tpFinalImage.setToggleGroup(toggleGroup);
		toggleGroup.selectToggle(tpFinalImage);
		tpFinalImage.setOnMouseClicked(event ->
		{
		    toggleGroup.selectToggle(tpFinalImage);
		    ivCenterImage.setImage(finalImage);
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
			candidateImageView.fitHeightProperty().bind(spCandidates.heightProperty().subtract(
											hScrollbarHeight[0] * 3 + 2)); // 2 = top border + bottom border
			String scoreTitle = AppUtils.replaceNumbersOnly(String.valueOf(candidate.getScore()), Locale.getDefault());
			
			GuiUtils.attachImageDialog(coreFxController, candidateImageView, scoreTitle,
			                           resources.getString("label.contextMenu.showImage"));
			
			ToggleTitledPane toggleTitledPane = new ToggleTitledPane(scoreTitle, candidateImageView);
			toggleTitledPane.setToggleGroup(toggleGroup);
			toggleTitledPane.setCollapsible(false);
			toggleTitledPane.setOnMouseClicked(event ->
			{
			    toggleGroup.selectToggle(toggleTitledPane);
			    ivCenterImage.setImage(candidateImage);
			    btnCompareWithUploadedImage.setDisable(false);
				GuiUtils.attachImageDialog(coreFxController, ivCenterImage, scoreTitle,
				                           resources.getString("label.contextMenu.showImage"));
			
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
	
	@FXML
	private void onCompareWithUploadedImageButtonClicked(ActionEvent actionEvent)
	{
		// get screen visual bounds
		Image selectedImage = ivCenterImage.getImage();
		
		if(finalImage.getHeight() >= selectedImage.getHeight())
		{
			double ratio = selectedImage.getHeight() / selectedImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = finalImage.getHeight() - selectedImage.getHeight();
			double extraWidth = heightDiff * ratio;
			selectedImage = scaleImage(selectedImage, selectedImage.getWidth() + extraWidth,
			                                                    selectedImage.getHeight() + heightDiff);
		}
		else
		{
			double ratio = finalImage.getHeight() / finalImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = selectedImage.getHeight() - finalImage.getHeight();
			double extraWidth = heightDiff * ratio;
			finalImage = scaleImage(finalImage, finalImage.getWidth() + extraWidth,
			                        finalImage.getHeight() + heightDiff);
		}
		
		String title = stringsBundle.getString("dialog.compare.title");
		String buttonText = stringsBundle.getString("dialog.compare.buttons.close");
		boolean rtl = coreFxController.getCurrentLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Image mergedImage;
		if(rtl) mergedImage = mergeImage(finalImage, selectedImage);
		else mergedImage = mergeImage(selectedImage, finalImage);
		
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
		dialogStage.getScene().getRoot().setOnContextMenuRequested(event ->
                        contextMenu.show(dialogStage.getScene().getRoot(), event.getScreenX(), event.getScreenY()));
		
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