package sa.gov.nic.bio.bw.workflow.cancellatent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.cancellatent.webservice.CancelLatentAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class CancelLatentWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long personId;
	@Input(alwaysRequired = true) private String latentId;
	@Output private Boolean success;
	
	@Override
	public void execute() throws Signal
	{
		CancelLatentAPI cancelLatentAPI = Context.getWebserviceManager().getApi(CancelLatentAPI.class);
		Call<Boolean> apiCall = cancelLatentAPI.cancelLatent(workflowId, workflowTcn, personId, latentId);
		TaskResponse<Boolean> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		success = taskResponse.getResult();
	}
}