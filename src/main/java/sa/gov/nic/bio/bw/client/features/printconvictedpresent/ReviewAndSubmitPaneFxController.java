package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryStatusResult;

import java.net.URL;
import java.util.Map;

public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase
{
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/reviewAndSubmit.fxml");
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