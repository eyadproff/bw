package sa.gov.nic.bio.bw.workflow.commons.tasks;

import com.google.gson.Gson;
import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FingerprintVerificationAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class FingerprintVerificationWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long personId;
	@Input(alwaysRequired = true) private Finger fingerprint;
	@Input(alwaysRequired = true) private Integer fingerPosition;
	@Output private Boolean matched;
	
	@Override
	public void execute() throws Signal
	{
		String fingerprintAsJson = new Gson().toJson(fingerprint, Finger.class);
		
		var api = Context.getWebserviceManager().getApi(FingerprintVerificationAPI.class);
		Call<Boolean> apiCall = api.verifyFingerprint(workflowId, workflowTcn, personId,
		                                              fingerprintAsJson, fingerPosition);
		TaskResponse<Boolean> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		matched = taskResponse.getResult();
	}
}