package sa.gov.nic.bio.bw.workflow.commons.tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.workflow.commons.webservice.Finger;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class FingerprintInquiryService
{
	public static TaskResponse<Integer> execute(List<Finger> collectedFingerprints,
	                                            List<Integer> missingFingerprints)
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
												Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		
		String a = new Gson().toJson(collectedFingerprints,
		                                            TypeToken.getParameterized(List.class, Finger.class).getType());
		String b = new Gson().toJson(missingFingerprints,
		                                            TypeToken.getParameterized(List.class, Integer.class).getType());
		
		Call<Integer> apiCall = fingerprintInquiryAPI.inquireWithFingerprints(a, b);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}