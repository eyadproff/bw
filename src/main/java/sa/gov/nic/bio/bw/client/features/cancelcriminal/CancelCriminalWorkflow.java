package sa.gov.nic.bio.bw.client.features.cancelcriminal;

import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.client.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.controllers.CancelCriminalPaneFxController;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.workflow.CancelCriminalWorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;

@AssociatedMenu(id = "menu.cancel.cancelCriminal", title = "menu.title", order = 1)
@WithLookups(SamisIdTypesLookup.class)
public class CancelCriminalWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(CancelCriminalPaneFxController.class);
		
		passData(CancelCriminalPaneFxController.class, CancelCriminalWorkflowTask.class,
		         "criminalId", "inquiryId", "personId", "samisIdType", "cancelCriminalMethod");
		
		executeTask(CancelCriminalWorkflowTask.class);
		passData(CancelCriminalWorkflowTask.class, CancelCriminalPaneFxController.class, "success");
	}
}