package sa.gov.nic.bio.bw.workflow.scfaceverification.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.faceverification.beans.FaceMatchingResponse;
import sa.gov.nic.bio.bw.workflow.scfaceverification.webservice.ScFaceVerificationAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class ScFaceVerificationWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long personId;
	@Input(alwaysRequired = true) private String facePhotoBase64;
	@Output private FaceMatchingResponse faceMatchingResponse;
	
	@Override
	public void execute() throws Signal
	{
		ScFaceVerificationAPI faceVerificationAPI = Context.getWebserviceManager().getApi(ScFaceVerificationAPI.class);
		Call<PersonInfo> apiCall = faceVerificationAPI.verifyFaceImage(workflowId, workflowTcn, personId,
		                                                               facePhotoBase64);
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