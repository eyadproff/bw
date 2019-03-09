package sa.gov.nic.bio.bw.workflow.cancellatent;

import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.workflow.cancellatent.controllers.CancelLatentPaneFxController;
import sa.gov.nic.bio.bw.workflow.cancellatent.tasks.CancelLatentWorkflowTask;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(workflowId = 1001, menuId = "menu.cancel.cancelLatent", menuTitle = "menu.title", menuOrder = 2)
public class CancelLatentWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(CancelLatentPaneFxController.class);
		
		passData(CancelLatentPaneFxController.class, CancelLatentWorkflowTask.class,
		         "personId", "latentId");
		
		executeWorkflowTask(CancelLatentWorkflowTask.class);
		passData(CancelLatentWorkflowTask.class, CancelLatentPaneFxController.class, "success");
	}
}