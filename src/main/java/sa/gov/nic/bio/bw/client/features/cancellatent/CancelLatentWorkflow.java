package sa.gov.nic.bio.bw.client.features.cancellatent;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancellatent.controllers.CancelLatentPaneFxController;
import sa.gov.nic.bio.bw.client.features.cancellatent.workflow.CancelLatentWorkflowTask;

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
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(CancelLatentPaneFxController.class);
		
		passData(CancelLatentPaneFxController.class, CancelLatentWorkflowTask.class,
		         "personId", "latentId");
		
		executeTask(CancelLatentWorkflowTask.class);
		passData(CancelLatentWorkflowTask.class, CancelLatentPaneFxController.class, "success");
	}
}