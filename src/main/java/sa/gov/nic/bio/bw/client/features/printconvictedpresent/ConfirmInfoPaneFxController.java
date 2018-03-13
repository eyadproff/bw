package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class ConfirmInfoPaneFxController extends WizardStepFxControllerBase
{
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/confirmInfo.fxml");
	}
	
	@Override
	protected void initialize()
	{
	
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
	
	}
}