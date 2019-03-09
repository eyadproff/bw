package sa.gov.nic.bio.bw.workflow.cancelcriminal;

import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.workflow.cancelcriminal.controllers.CancelCriminalPaneFxController;
import sa.gov.nic.bio.bw.workflow.cancelcriminal.tasks.CancelCriminalWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;

@AssociatedMenu(workflowId = 1000, menuId = "menu.cancel.cancelCriminal", menuTitle = "menu.title", menuOrder = 1)
@WithLookups(PersonTypesLookup.class)
public class CancelCriminalWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(CancelCriminalPaneFxController.class);
		
		passData(CancelCriminalPaneFxController.class, CancelCriminalWorkflowTask.class,
		         "criminalId", "inquiryId", "personId", "samisIdType", "cancelCriminalMethod");
		
		executeWorkflowTask(CancelCriminalWorkflowTask.class);
		passData(CancelCriminalWorkflowTask.class, CancelCriminalPaneFxController.class, "success");
	}
}