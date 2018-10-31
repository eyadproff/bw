package sa.gov.nic.bio.bw.client.features.cancellatent;

import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancellatent.controllers.CancelLatentPaneFxController;
import sa.gov.nic.bio.bw.client.features.cancellatent.workflow.CancelLatentWorkflowTask;

@AssociatedMenu(id = "menu.cancel.cancelLatent", title = "menu.title", order = 2)
public class CancelLatentWorkflow extends SinglePageWorkflowBase
{
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