package sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.Decision;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice.LatentAPI;

public class AddDecisionToLatentHitWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long transactionNumber;
	@Input(alwaysRequired = true) private Decision decision;
	@Input private String latentNumber;
	@Input private Long civilBiometricsId;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(LatentAPI.class);
		var call = api.addDecisionToLatentHit(workflowId, workflowTcn, transactionNumber, decision, latentNumber, civilBiometricsId);
		var taskResponse = Context.getWebserviceManager().executeApi(call);
		
		resetWorkflowStepIfNegativeTaskResponse(taskResponse);
	}
}