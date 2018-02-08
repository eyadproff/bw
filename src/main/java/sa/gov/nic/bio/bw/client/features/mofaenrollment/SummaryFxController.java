package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class SummaryFxController extends WizardStepFxControllerBase
{
	@FXML private Button btnPrevious;
	@FXML private Button btnSubmit;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/summary.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(event -> goPrevious());
		btnSubmit.setOnAction(event -> goNext());
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