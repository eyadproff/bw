package sa.gov.nic.bio.bw.workflow.latentreversesearch;

import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers.LatentReverseSearchPaneFxController;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks.LatentHitsInquiryBySearchCriteriaWorkflowTask;

@AssociatedMenu(workflowId = 1026, menuId = "menu.query.latentReverseSearch", menuTitle = "menu.title", menuOrder = 9)
public class LatentReverseSearchWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(LatentReverseSearchPaneFxController.class);
		
		passData(LatentReverseSearchPaneFxController.class, LatentHitsInquiryBySearchCriteriaWorkflowTask.class,
		         "transactionNumber", "civilBiometricsId", "personId", "referenceNumber",
		         "locationId", "status", "entryDateFrom", "entryDateTo", "recordsPerPage", "pageIndex");
		
		executeWorkflowTask(LatentHitsInquiryBySearchCriteriaWorkflowTask.class);
		
		passData(LatentHitsInquiryBySearchCriteriaWorkflowTask.class,
		         LatentReverseSearchPaneFxController.class, "resultsTotalCount", "latentHits");
	}
}