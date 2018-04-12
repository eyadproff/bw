package sa.gov.nic.bio.bw.client.features.printconvictedpresent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class FingerprintInquiryStatusCheckerService
{
	public static ServiceResponse<FingerprintInquiryStatusResult> execute(int inquiryId)
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
												Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.checkFingerprintsInquiryStatus");
		
		Call<FingerprintInquiryStatusResult> apiCall = fingerprintInquiryAPI.checkFingerprintsInquiryStatus(url,
		                                                                                                    inquiryId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}