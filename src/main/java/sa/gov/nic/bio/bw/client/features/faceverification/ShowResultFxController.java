package sa.gov.nic.bio.bw.client.features.faceverification;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceMatchingResponse;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceMatchingResponse.PersonInfo;

import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class ShowResultFxController extends WizardStepFxControllerBase
{
	public static final String KEY_FACE_MATCHING_RESPONSE = "FACE_MATCHING_RESPONSE";
	
	@FXML private Pane matchedPane;
	@FXML private Pane notMatchedPane;
	@FXML private Pane noFacePane;
	@FXML private Pane imagePane;
	@FXML private ImageView ivUploadedImage;
	@FXML private ImageView ivDBImage;
	@FXML private Label lblNotMatched;
	@FXML private Label lblNoFace;
	@FXML private Label lblBioId;
	@FXML private Label lblSamisId;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblFamilyName;
	@FXML private Button btnCompareWithUploadedImage;
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/showResult.fxml");
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
		ivUploadedImage.fitWidthProperty().bind(imagePane.widthProperty().divide(2.5));
		ivUploadedImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.2));
		ivDBImage.fitWidthProperty().bind(imagePane.widthProperty().divide(2.5));
		ivDBImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.2));
		imagePane.autosize();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			long personId = (long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_ID);
			FaceMatchingResponse faceMatchingResponse = (FaceMatchingResponse)
																		uiInputData.get(KEY_FACE_MATCHING_RESPONSE);
			
			if(faceMatchingResponse.isNoFace())
			{
				GuiUtils.showNode(noFacePane, true);
				lblNoFace.setText(String.format(resources.getString("label.noFaceForPersonId"),
				                                String.valueOf(personId)));
			}
			else if(!faceMatchingResponse.isMatched())
			{
				GuiUtils.showNode(notMatchedPane, true);
				lblNotMatched.setText(String.format(resources.getString("label.faceImageIsNotMatched"),
				                                    String.valueOf(personId)));
			}
			else // matched
			{
				GuiUtils.showNode(matchedPane, true);
				GuiUtils.showNode(imagePane, true);
				GuiUtils.showNode(btnCompareWithUploadedImage, true);
				
				PersonInfo personInfo = faceMatchingResponse.getPersonInfo();
				long bioId = personInfo.getBioId();
				long samisId = personInfo.getSamisId();
				String firstName = personInfo.getFirstName();
				String fatherName = personInfo.getFatherName();
				String familyName = personInfo.getFamilyName();
				
				// default values
				lblBioId.setText(resources.getString("label.notAvailable"));
				lblSamisId.setText(resources.getString("label.notAvailable"));
				lblFirstName.setText(resources.getString("label.notAvailable"));
				lblFatherName.setText(resources.getString("label.notAvailable"));
				lblFamilyName.setText(resources.getString("label.notAvailable"));
				
				if(firstName != null && firstName.trim().isEmpty()) firstName = null;
				if(fatherName != null && fatherName.trim().isEmpty()) fatherName = null;
				if(familyName != null && familyName.trim().isEmpty()) familyName = null;
				
				String sBioId = AppUtils.replaceNumbersOnly(String.valueOf(bioId), Locale.getDefault());
				lblBioId.setText(sBioId);
				
				if(samisId > 0)
				{
					String sSamisId = AppUtils.replaceNumbersOnly(String.valueOf(samisId), Locale.getDefault());
					lblSamisId.setText(sSamisId);
				}
				
				if(firstName != null) lblFirstName.setText(firstName);
				if(fatherName != null) lblFatherName.setText(fatherName);
				if(familyName != null) lblFamilyName.setText(familyName);
				
				Image uploadedImage = (Image) uiInputData.get(ConfirmInputFxController.KEY_FINAL_IMAGE);
				Image dbImage = AppUtils.imageFromBase64(personInfo.getImage());
				
				ivUploadedImage.setImage(uploadedImage);
				ivDBImage.setImage(dbImage);
				
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivUploadedImage,
				                           resources.getString("label.uploadedImage"),
				                           resources.getString("label.contextMenu.showImage"), false);
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivDBImage,
				                           resources.getString("label.dbImage"),
				                           resources.getString("label.contextMenu.showImage"), false);
			}
		}
	}
	
	@FXML
	private void onCompareWithUploadedImageButtonClicked(ActionEvent actionEvent)
	{
		Image uploadedImage = ivUploadedImage.getImage();
		Image dbImage = ivDBImage.getImage();
		
		if(uploadedImage.getHeight() >= dbImage.getHeight())
		{
			double ratio = dbImage.getHeight() / dbImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = uploadedImage.getHeight() - dbImage.getHeight();
			double extraWidth = heightDiff * ratio;
			dbImage = GuiUtils.scaleImage(dbImage, dbImage.getWidth() + extraWidth,
			                                    dbImage.getHeight() + heightDiff);
		}
		else
		{
			double ratio = uploadedImage.getHeight() / uploadedImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = dbImage.getHeight() - uploadedImage.getHeight();
			double extraWidth = heightDiff * ratio;
			uploadedImage = GuiUtils.scaleImage(uploadedImage, uploadedImage.getWidth() + extraWidth,
			                                 uploadedImage.getHeight() + heightDiff);
		}
		
		String title = resources.getString("dialog.compare.title");
		String buttonText = resources.getString("dialog.compare.buttons.close");
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Image mergedImage;
		if(rtl) mergedImage = GuiUtils.mergeImage(uploadedImage, dbImage);
		else mergedImage = GuiUtils.mergeImage(dbImage, uploadedImage);
		
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