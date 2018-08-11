package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification;

import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;

import java.util.Map;

public class FingerprintCardIdentificationPaneFxController extends BodyFxControllerBase
{
	@Override
	protected void initialize()
	{
	
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		System.out.println("newForm = " + newForm);
		
		if(newForm)
		{
		
		}
	}
}