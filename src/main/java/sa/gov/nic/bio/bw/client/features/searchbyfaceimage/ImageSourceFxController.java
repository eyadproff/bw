package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
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
		btnNext.setOnAction(event -> goNext());
	}
	
	@Override
	public void onControllerReady()
	{
		rbByUploadingImage.setOnAction(event ->
		{
			coreFxController.getWizardPane().updateStep(1,
			                                            resources.getString("wizard.uploadImage"),
			                                            "upload");
		});
		
		rbByCamera.setOnAction(event ->
		{
			coreFxController.getWizardPane().updateStep(1,
			                                            resources.getString("wizard.capturePhotoByCamera"),
			                                            "camera");
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			String imageSource = (String) uiInputData.get(SearchByFaceImageWorkflow.KEY_IMAGE_SOURCE);
			
			if(SearchByFaceImageWorkflow.VALUE_IMAGE_SOURCE_CAMERA.equals(imageSource)) rbByCamera.setSelected(true);
			else rbByUploadingImage.setSelected(true);
		}
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		if(rbByCamera.isSelected()) uiDataMap.put(SearchByFaceImageWorkflow.KEY_IMAGE_SOURCE,
		                                          SearchByFaceImageWorkflow.VALUE_IMAGE_SOURCE_CAMERA);
		else uiDataMap.put(SearchByFaceImageWorkflow.KEY_IMAGE_SOURCE,
		                   SearchByFaceImageWorkflow.VALUE_IMAGE_SOURCE_UPLOAD);
	}
}