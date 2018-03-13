package sa.gov.nic.bio.bw.client.features.printconvictednotpresent;

import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class FetchingFingerprintsPaneFxController extends WizardStepFxControllerBase
{
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/fetchingFingerprints.fxml");
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