package sa.gov.nic.bio.bw.workflow.criminaltransactions;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.controllers.CriminalTransactionsPaneFxController;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.lookups.CriminalTransactionTypesLookup;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.tasks.CriminalTransactionsInquiryWorkflowTask;

@AssociatedMenu(workflowId = 1023, menuId = "menu.query.criminalTransactions",
				menuTitle = "menu.title", menuOrder = 8, devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class, CriminalTransactionTypesLookup.class})
public class CriminalTransactionsWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(CriminalTransactionsPaneFxController.class);
		
		passData(CriminalTransactionsPaneFxController.class, CriminalTransactionsInquiryWorkflowTask.class,
		         "criminalBiometricsId");
		
		executeWorkflowTask(CriminalTransactionsInquiryWorkflowTask.class);
		
		passData(CriminalTransactionsInquiryWorkflowTask.class,
		         CriminalTransactionsPaneFxController.class, "criminalTransactions");
		passData(CriminalTransactionsInquiryWorkflowTask.class,
		         CriminalTransactionsPaneFxController.class, "resultsTotalCount");
	}
}