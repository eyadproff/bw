package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ImageSourceFxController extends WizardStepFxControllerBase
{
	public static final String KEY_HIDE_IMAGE_SOURCE_PREVIOUS_BUTTON = "HIDE_IMAGE_SOURCE_PREVIOUS_BUTTON";
	public static final String KEY_IMAGE_SOURCE = "IMAGE_SOURCE";
	public static final String VALUE_IMAGE_SOURCE_UPLOAD = "IMAGE_SOURCE_UPLOAD";
	public static final String VALUE_IMAGE_SOURCE_CAMERA = "IMAGE_SOURCE_CAMERA";
	
	@FXML private ResourceBundle resources;
	@FXML private RadioButton rbByUploadingImage;
	@FXML private RadioButton rbByCamera;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/imageSource.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnPrevious);
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
		
		// go next on pressing ENTER on the radio buttons
		EventHandler<KeyEvent> eventHandler = event ->
		{
			if(event.getCode() == KeyCode.ENTER)
			{
				btnNext.fire();
				event.consume();
			}
		};
		rbByUploadingImage.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByCamera.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
	}
	
	@Override
	protected void onAttachedToScene()
	{
		String uploadImageTitle = resources.getString("wizard.uploadImage");
		String capturePhotoByCameraTitle = resources.getString("wizard.capturePhotoByCamera");
		
		// change the wizard-step-indicator upon changing the image source
		int stepIndex = Context.getCoreFxController().getWizardPane().getStepIndexByTitle(uploadImageTitle);
		if(stepIndex < 0) stepIndex = Context.getCoreFxController().getWizardPane()
																   .getStepIndexByTitle(capturePhotoByCameraTitle);
		
		final int finalStepIndex = stepIndex;
		
		rbByUploadingImage.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue) Context.getCoreFxController().getWizardPane().updateStep(finalStepIndex, uploadImageTitle,
			                                                                      "upload");
			else Context.getCoreFxController().getWizardPane().updateStep(finalStepIndex, capturePhotoByCameraTitle,
			                                                              "camera");
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			// collect configurations from the workflow
			Boolean hidePreviousButton = (Boolean) uiInputData.get(KEY_HIDE_IMAGE_SOURCE_PREVIOUS_BUTTON);
			if(hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);
			
			// load the old state, if exists
			String imageSource = (String) uiInputData.get(KEY_IMAGE_SOURCE);
			if(VALUE_IMAGE_SOURCE_CAMERA.equals(imageSource))
			{
				rbByCamera.setSelected(true);
				rbByCamera.requestFocus();
			}
			else
			{
				rbByUploadingImage.setSelected(true);
				rbByUploadingImage.requestFocus();
			}
		}
	}
	
	@Override
	public void onLeaving(Map<String, Object> uiDataMap)
	{
		// save the selected source of image into the map
		if(rbByCamera.isSelected()) uiDataMap.put(KEY_IMAGE_SOURCE, VALUE_IMAGE_SOURCE_CAMERA);
		else uiDataMap.put(KEY_IMAGE_SOURCE, VALUE_IMAGE_SOURCE_UPLOAD);
	}
}