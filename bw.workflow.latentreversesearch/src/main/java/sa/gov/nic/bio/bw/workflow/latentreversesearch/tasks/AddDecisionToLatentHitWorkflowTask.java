package sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.DecisionHistory;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice.LatentAPI;

public class AddDecisionToLatentHitWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private DecisionHistory decisionHistory;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(LatentAPI.class);
		String decisionHistoryJson = AppUtils.toJson(decisionHistory);
		var call = api.addDecisionToLatentHit(workflowId, workflowTcn, decisionHistoryJson);
		var taskResponse = Context.getWebserviceManager().executeApi(call);
		
		resetWorkflowStepIfNegativeTaskResponse(taskResponse);
	}
}