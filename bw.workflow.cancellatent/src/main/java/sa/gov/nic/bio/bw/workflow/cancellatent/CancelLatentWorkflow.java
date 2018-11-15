package sa.gov.nic.bio.bw.workflow.cancellatent;

import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.workflow.cancellatent.controllers.CancelLatentPaneFxController;
import sa.gov.nic.bio.bw.workflow.cancellatent.tasks.CancelLatentWorkflowTask;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(id = "menu.cancel.cancelLatent", title = "menu.title", order = 2)
public class CancelLatentWorkflow extends SinglePageWorkflowBase
{
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
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