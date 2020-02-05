package sa.gov.nic.bio.bw.workflow.criminaltransactions;

import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.controllers.CriminalTransactionsPaneFxController;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.lookups.CriminalTransactionTypesLookup;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.tasks.CriminalTransactionsInquiryWorkflowTask;

@AssociatedMenu(workflowId = 1023, menuId = "menu.query.criminalTransactions",
				menuTitle = "menu.title", menuOrder = 8)
@WithLookups({CriminalTransactionTypesLookup.class})
public class CriminalTransactionsWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(CriminalTransactionsPaneFxController.class);
		
		passData(CriminalTransactionsPaneFxController.class, CriminalTransactionsInquiryWorkflowTask.class,
		         "criminalBiometricsId", "reportNumber", "location", "operatorId",
		         "criminalDelinkId", "transactionType", "recordsPerPage", "pageIndex");
		
		executeWorkflowTask(CriminalTransactionsInquiryWorkflowTask.class);
		
		passData(CriminalTransactionsInquiryWorkflowTask.class,
		         CriminalTransactionsPaneFxController.class, "criminalTransactions");
		passData(CriminalTransactionsInquiryWorkflowTask.class,
		         CriminalTransactionsPaneFxController.class, "resultsTotalCount");
	}
}