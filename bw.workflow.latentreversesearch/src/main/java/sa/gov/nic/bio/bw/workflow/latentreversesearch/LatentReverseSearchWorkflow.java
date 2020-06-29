package sa.gov.nic.bio.bw.workflow.latentreversesearch;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers.LatentReverseSearchPaneFxController;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers.LatentReverseSearchPaneFxController.Request;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks.AddDecisionToLatentHitWorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks.LatentJobsInquiryBySearchCriteriaWorkflowTask;

@AssociatedMenu(workflowId = 1026, menuId = "menu.query.latentReverseSearch", menuTitle = "menu.title", menuOrder = 9,
				devices = {Device.BIO_UTILITIES})
public class LatentReverseSearchWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(LatentReverseSearchPaneFxController.class);
		
		Request request = getData(LatentReverseSearchPaneFxController.class, "request");
		if(request == Request.SEARCH)
		{
			passData(LatentReverseSearchPaneFxController.class, LatentJobsInquiryBySearchCriteriaWorkflowTask.class,
			         "jobId", "civilBiometricsId", "personId", "tcn",
			         "locationId", "status", "createDateFrom", "createDateTo", "recordsPerPage", "pageIndex");
			
			executeWorkflowTask(LatentJobsInquiryBySearchCriteriaWorkflowTask.class);
			
			passData(LatentJobsInquiryBySearchCriteriaWorkflowTask.class,
			         LatentReverseSearchPaneFxController.class, "resultsTotalCount", "latentJobs");
		}
		else if(request == Request.SUBMIT_DECISION)
		{
			passData(LatentReverseSearchPaneFxController.class, AddDecisionToLatentHitWorkflowTask.class,
			         "decisionHistory");
			
			executeWorkflowTask(AddDecisionToLatentHitWorkflowTask.class);
		}
	}
}