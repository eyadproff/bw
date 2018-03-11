package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow.SearchByFaceImageWorkflow;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ImageSourceFxController extends WizardStepFxControllerBase
{
	@FXML private ResourceBundle resources;
	@FXML private RadioButton rbByUploadingImage;
	@FXML private RadioButton rbByCamera;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/imageSource.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
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
		// change the wizard-step-indicator upon changing the image source
		int INSERTING_IMAGE_STEP_INDEX = 1;
		rbByUploadingImage.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue) coreFxController.getWizardPane().updateStep(INSERTING_IMAGE_STEP_INDEX,
			                                                         resources.getString("wizard.uploadImage"),
			                                                         "upload");
			else coreFxController.getWizardPane().updateStep(INSERTING_IMAGE_STEP_INDEX,
			                                                 resources.getString("wizard.capturePhotoByCamera"),
			                                                 "camera");
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			// load the old state, if exists
			String imageSource = (String) uiInputData.get(SearchByFaceImageWorkflow.KEY_IMAGE_SOURCE);
			if(SearchByFaceImageWorkflow.VALUE_IMAGE_SOURCE_CAMERA.equals(imageSource))
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
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		// save the selected source of image into the map
		if(rbByCamera.isSelected()) uiDataMap.put(SearchByFaceImageWorkflow.KEY_IMAGE_SOURCE,
		                                          SearchByFaceImageWorkflow.VALUE_IMAGE_SOURCE_CAMERA);
		else uiDataMap.put(SearchByFaceImageWorkflow.KEY_IMAGE_SOURCE,
		                   SearchByFaceImageWorkflow.VALUE_IMAGE_SOURCE_UPLOAD);
	}
}