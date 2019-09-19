package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FaceVerificationAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class FaceVerificationWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long personId;
	@Input(alwaysRequired = true) private String facePhotoBase64;
	@Output private Boolean matched;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(FaceVerificationAPI.class);
		Call<Boolean> apiCall = api.verifyFace(workflowId, workflowTcn, personId, facePhotoBase64);
		TaskResponse<Boolean> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		matched = taskResponse.getResult();
	}
}