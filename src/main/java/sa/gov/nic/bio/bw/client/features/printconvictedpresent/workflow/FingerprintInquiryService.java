package sa.gov.nic.bio.bw.client.features.printconvictedpresent.workflow;

import com.google.gson.Gson;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class FingerprintInquiryService
{
	public static ServiceResponse<List<Integer>> execute(List<Finger> collectedFingerprints,
	                                                     List<Integer> missingFingerprints)
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
												Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.inquireWithFingerprints");
		
		String a = new Gson().toJson(collectedFingerprints);
		String b = new Gson().toJson(missingFingerprints);
		
		System.out.println("a = " + a);
		System.out.println("b = " + b);
		
		Call<List<Integer>> apiCall = fingerprintInquiryAPI.inquireWithFingerprints(url, a, b);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}