package sa.gov.nic.bio.bw.client.features.faceverification.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceMatchingResponse;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceVerificationAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class FaceVerificationWorkflowTask implements WorkflowTask
{
	@Input(required = true) private Long personId;
	@Input(required = true) private String faceImageBase64;
	@Output private FaceMatchingResponse faceMatchingResponse;
	
	@Override
	public ServiceResponse<?> execute()
	{
		FaceVerificationAPI faceVerificationAPI = Context.getWebserviceManager().getApi(FaceVerificationAPI.class);
		Call<PersonInfo> apiCall = faceVerificationAPI.verifyFaceImage(personId, faceImageBase64);
		ServiceResponse<PersonInfo> serviceResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		if(serviceResponse.isSuccess())
		{
			PersonInfo personInfo = serviceResponse.getResult();
			faceMatchingResponse = new FaceMatchingResponse(true, personInfo);
			return serviceResponse;
		}
		else
		{
			if("B003-0021".equals(serviceResponse.getErrorCode())) // not matched
			{
				faceMatchingResponse = new FaceMatchingResponse(false, null);
				return ServiceResponse.success();
			}
			else return serviceResponse;
		}
	}
}