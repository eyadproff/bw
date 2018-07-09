package sa.gov.nic.bio.bw.client.features.faceverification.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceVerificationAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class FaceVerificationService
{
	public static ServiceResponse<PersonInfo> execute(long personId, String imageBase64)
	{
		FaceVerificationAPI faceVerificationAPI = Context.getWebserviceManager().getApi(FaceVerificationAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.faceVerification");
		Call<PersonInfo> apiCall = faceVerificationAPI.verifyFaceImage(url, personId, imageBase64);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}