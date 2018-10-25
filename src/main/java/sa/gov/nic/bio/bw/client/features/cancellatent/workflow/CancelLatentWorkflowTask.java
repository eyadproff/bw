package sa.gov.nic.bio.bw.client.features.cancellatent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.cancellatent.webservice.CancelLatentAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class CancelLatentWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long personId;
	@Input(alwaysRequired = true) private String latentId;
	@Output private Boolean success;
	
	@Override
	public void execute() throws Signal
	{
		CancelLatentAPI cancelLatentAPI = Context.getWebserviceManager().getApi(CancelLatentAPI.class);
		Call<Boolean> apiCall = cancelLatentAPI.cancelLatent(personId, latentId);
		TaskResponse<Boolean> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		success = taskResponse.getResult();
	}
}