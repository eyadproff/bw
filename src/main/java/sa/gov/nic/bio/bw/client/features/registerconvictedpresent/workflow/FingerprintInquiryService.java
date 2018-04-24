package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class FingerprintInquiryService
{
	public static ServiceResponse<Integer> execute(List<Finger> collectedFingerprints,
	                                                     List<Integer> missingFingerprints)
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
												Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.inquireWithFingerprints");
		
		String a = new Gson().toJson(collectedFingerprints,
		                                            TypeToken.getParameterized(List.class, Finger.class).getType());
		String b = new Gson().toJson(missingFingerprints,
		                                            TypeToken.getParameterized(List.class, Integer.class).getType());
		
		Call<Integer> apiCall = fingerprintInquiryAPI.inquireWithFingerprints(url, a, b);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}