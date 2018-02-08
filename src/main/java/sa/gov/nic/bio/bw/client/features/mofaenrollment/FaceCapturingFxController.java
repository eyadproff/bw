package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class FaceCapturingFxController extends WizardStepFxControllerBase
{
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/faceCapturing.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
	}
	
	@Override
	public void onControllerReady()
	{
	
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
	
	}
}