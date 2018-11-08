package sa.gov.nic.bio.bw.features.faceverification.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.features.faceverification.webservice.FaceMatchingResponse;
import sa.gov.nic.bio.bw.features.faceverification.webservice.FaceVerificationAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class FaceVerificationWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private Long personId;
	@Input(alwaysRequired = true) private String faceImageBase64;
	@Output private FaceMatchingResponse faceMatchingResponse;
	
	@Override
	public void execute() throws Signal
	{
		FaceVerificationAPI faceVerificationAPI = Context.getWebserviceManager().getApi(FaceVerificationAPI.class);
		Call<PersonInfo> apiCall = faceVerificationAPI.verifyFaceImage(personId, faceImageBase64);
		TaskResponse<PersonInfo> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		if(taskResponse.isSuccess())
		{
			PersonInfo personInfo = taskResponse.getResult();
			faceMatchingResponse = new FaceMatchingResponse(true, personInfo);
		}
		else
		{
			if("B003-0021".equals(taskResponse.getErrorCode())) // not matched
			{
				faceMatchingResponse = new FaceMatchingResponse(false, null);
			}
			else resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		}
	}
}