package sa.gov.nic.bio.bw.client.features.cancellatent.workflow;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancellatent.CancelLatentPaneFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CancelLatentWorkflow extends WizardWorkflowBase<Void, Void>
{
	public CancelLatentWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		Context.getCoreFxController().clearWizardBar();
		renderUi(CancelLatentPaneFxController.class);
		waitForUserInput();
		
		Long personId = (Long) uiInputData.get("personId");
		String latentId = (String) uiInputData.get("latentId");
		uiInputData.clear();
		
		ServiceResponse<Boolean> response = CancelLatentService.execute(personId, latentId);
		uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
	}
}