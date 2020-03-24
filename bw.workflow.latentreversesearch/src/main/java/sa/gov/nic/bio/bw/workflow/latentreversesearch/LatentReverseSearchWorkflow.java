package sa.gov.nic.bio.bw.workflow.latentreversesearch;

import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.Decision;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers.LatentReverseSearchPaneFxController;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers.LatentReverseSearchPaneFxController.Request;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks.AddDecisionToLatentHitWorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks.LatentJobsInquiryBySearchCriteriaWorkflowTask;

@AssociatedMenu(workflowId = 1026, menuId = "menu.query.latentReverseSearch", menuTitle = "menu.title", menuOrder = 9)
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
		else if(request == Request.LINK_LATENT)
		{
			passData(LatentReverseSearchPaneFxController.class, AddDecisionToLatentHitWorkflowTask.class,
			         "jobId", "civilBiometricsId", "latentNumber");
			setData(AddDecisionToLatentHitWorkflowTask.class, "decision", Decision.LATENT_ASSOCIATED);
			
			executeWorkflowTask(AddDecisionToLatentHitWorkflowTask.class);
		}
		else if(request == Request.FINISH_WITHOUT_LINKING_LATENT)
		{
			passData(LatentReverseSearchPaneFxController.class, AddDecisionToLatentHitWorkflowTask.class,
			         "jobId");
			setData(AddDecisionToLatentHitWorkflowTask.class, "decision", Decision.FINISHED_WITHOUT_ASSOCIATING_LATENT);
			
			executeWorkflowTask(AddDecisionToLatentHitWorkflowTask.class);
		}
		else if(request == Request.REVERT_TO_NEW)
		{
			passData(LatentReverseSearchPaneFxController.class, AddDecisionToLatentHitWorkflowTask.class,
			         "jobId");
			setData(AddDecisionToLatentHitWorkflowTask.class, "decision", Decision.VIEW_WITHOUT_ACTION);
			
			executeWorkflowTask(AddDecisionToLatentHitWorkflowTask.class);
		}
	}
}