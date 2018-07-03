package sa.gov.nic.bio.bw.client.features.faceverification.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceMatchingResponse;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceVerificationAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class FaceVerificationService
{
	public static ServiceResponse<FaceMatchingResponse> execute(long personId, String imageBase64)
	{
		FaceVerificationAPI faceVerificationAPI = Context.getWebserviceManager().getApi(FaceVerificationAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.faceVerification");
		Call<FaceMatchingResponse> apiCall = faceVerificationAPI.verifyFaceImage(url, personId, imageBase64);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}