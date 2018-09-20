package sa.gov.nic.bio.bw.client.features.cancellatent;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancellatent.controllers.CancelLatentPaneFxController;
import sa.gov.nic.bio.bw.client.features.cancellatent.workflow.CancelLatentService;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.cancel.cancelLatent", title = "menu.title", order = 2)
public class CancelLatentWorkflow extends SinglePageWorkflowBase
{
	public CancelLatentWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public boolean onStep() throws InterruptedException, Signal
	{
		renderUi(CancelLatentPaneFxController.class);
		waitForUserInput();
		
		Long personId = (Long) uiInputData.get("personId");
		String latentId = (String) uiInputData.get("latentId");
		uiInputData.clear();
		
		ServiceResponse<Boolean> response = CancelLatentService.execute(personId, latentId);
		uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
		
		return true;
	}
}