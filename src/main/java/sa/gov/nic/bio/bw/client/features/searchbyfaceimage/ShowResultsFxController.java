package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
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
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ui.ToggleTitledPane;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShowResultsFxController extends WizardStepFxControllerBase
{
	@Input(required = true) private Image finalImage;
	@Input(required = true) private List<Candidate> candidates;
	
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
	protected void onAttachedToScene()
	{
		imagePane.maxWidthProperty().bind(Context.getCoreFxController().getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(Context.getCoreFxController().getBodyPane().heightProperty());
		ivCenterImage.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivCenterImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(0.0));
		
		ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) ->
		{
			if(!newValue) // on un-maximize (workaround to fix JavaFX bug)
			{
				Platform.runLater(() ->
				{
				    imagePane.autosize();
				    Context.getCoreFxController().getBodyPane().autosize();
				});
			}
		};
		Context.getCoreFxController().getStage().maximizedProperty().addListener(changeListener);
		imagePane.sceneProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue == null) Context.getCoreFxController().getStage().maximizedProperty()
				                                                                .removeListener(changeListener);
		});
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
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
		imageView.setImage(finalImage);
		imageView.setPreserveRatio(true);
		
		final double[] hScrollbarHeight = {13.0};
		imageView.fitHeightProperty().bind(spCandidates.heightProperty()
                                   .subtract(hScrollbarHeight[0] * 3 + 2)); // 2 = top border + bottom border
		GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView, tpFinalImage.getText(),
		                           resources.getString("label.contextMenu.showImage"), false);
		
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
		GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCenterImage, tpFinalImage.getText(),
		                           resources.getString("label.contextMenu.showImage"), false);
		
		tpFinalImage.setContent(imageView);
		ToggleGroup toggleGroup = new ToggleGroup();
		tpFinalImage.setToggleGroup(toggleGroup);
		toggleGroup.selectToggle(tpFinalImage);
		tpFinalImage.setOnMouseClicked(event ->
		{
		    toggleGroup.selectToggle(tpFinalImage);
		    ivCenterImage.setImage(finalImage);
		    btnCompareWithUploadedImage.setDisable(true);
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCenterImage, tpFinalImage.getText(),
			                           resources.getString("label.contextMenu.showImage"), false);
		
		    lblBioId.setText(resources.getString("label.notAvailable"));
		    lblScore.setText(resources.getString("label.notAvailable"));
		    lblSamisId.setText(resources.getString("label.notAvailable"));
		    lblFirstName.setText(resources.getString("label.notAvailable"));
		    lblFatherName.setText(resources.getString("label.notAvailable"));
		    lblFamilyName.setText(resources.getString("label.notAvailable"));
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
			String scoreTitle = AppUtils.localizeNumbers(String.valueOf(candidate.getScore()));
			
			GuiUtils.attachImageDialog(Context.getCoreFxController(), candidateImageView, scoreTitle,
			                           resources.getString("label.contextMenu.showImage"), false);
			
			ToggleTitledPane toggleTitledPane = new ToggleTitledPane(scoreTitle, candidateImageView);
			toggleTitledPane.setToggleGroup(toggleGroup);
			toggleTitledPane.setCollapsible(false);
			toggleTitledPane.setFocusTraversable(false);
			toggleTitledPane.setOnMouseClicked(event ->
			{
			    toggleGroup.selectToggle(toggleTitledPane);
			    ivCenterImage.setImage(candidateImage);
			    btnCompareWithUploadedImage.setDisable(false);
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCenterImage, scoreTitle,
				                           resources.getString("label.contextMenu.showImage"), false);
			
			    // default values
			    lblBioId.setText(resources.getString("label.notAvailable"));
			    lblScore.setText(resources.getString("label.notAvailable"));
			    lblSamisId.setText(resources.getString("label.notAvailable"));
			    lblFirstName.setText(resources.getString("label.notAvailable"));
			    lblFatherName.setText(resources.getString("label.notAvailable"));
			    lblFamilyName.setText(resources.getString("label.notAvailable"));
			
			    long bioId = candidate.getBioId();
			    int score = candidate.getScore();
			    long samisId = candidate.getSamisId();
			    String firstName = candidate.getFirstName();
			    String fatherName = candidate.getFatherName();
			    String familyName = candidate.getFamilyName();
			
			    if(firstName != null && (firstName.trim().isEmpty() || firstName.trim().equals("-"))) firstName = null;
			    if(fatherName != null && (fatherName.trim().isEmpty() || fatherName.trim().equals("-")))
			    	                                                                                fatherName = null;
			    if(familyName != null && (familyName.trim().isEmpty() || familyName.trim().equals("-")))
			    	                                                                                familyName = null;
			
			    String sBioId = AppUtils.localizeNumbers(String.valueOf(bioId));
			    String sScore = AppUtils.localizeNumbers(String.valueOf(score));
			
			    lblBioId.setText(sBioId);
			    lblScore.setText(sScore);
			
			    if(samisId > 0)
			    {
			        String sSamisId = AppUtils.localizeNumbers(String.valueOf(samisId));
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
		Image selectedImage = ivCenterImage.getImage();
		
		if(finalImage.getHeight() >= selectedImage.getHeight())
		{
			double ratio = selectedImage.getHeight() / selectedImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = finalImage.getHeight() - selectedImage.getHeight();
			double extraWidth = heightDiff * ratio;
			selectedImage = GuiUtils.scaleImage(selectedImage, selectedImage.getWidth() + extraWidth,
			                                                    selectedImage.getHeight() + heightDiff);
		}
		else
		{
			double ratio = finalImage.getHeight() / finalImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = selectedImage.getHeight() - finalImage.getHeight();
			double extraWidth = heightDiff * ratio;
			finalImage = GuiUtils.scaleImage(finalImage, finalImage.getWidth() + extraWidth,
			                        finalImage.getHeight() + heightDiff);
		}
		
		String title = resources.getString("dialog.compare.title");
		String buttonText = resources.getString("dialog.compare.buttons.close");
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Image mergedImage;
		if(rtl) mergedImage = GuiUtils.mergeImage(finalImage, selectedImage);
		else mergedImage = GuiUtils.mergeImage(selectedImage, finalImage);
		
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeMenuItem = new MenuItem(buttonText);
		contextMenu.getItems().add(closeMenuItem);
		
		Button btnClose = new Button(buttonText);
		btnClose.requestFocus();
		btnClose.setPadding(new Insets(10));
		GuiUtils.makeButtonClickableByPressingEnter(btnClose);
		
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
		
		Stage dialogStage = DialogUtils.buildCustomDialog(Context.getCoreFxController().getStage(), title,
		                                                  stackPane, rtl, false);
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
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
}