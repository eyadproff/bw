package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.commons.TaskResponse;

public class FingerprintInquiryStatusCheckerService
{
	public static TaskResponse<FingerprintInquiryStatusResult> execute(int inquiryId)
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
												Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		Call<FingerprintInquiryStatusResult> apiCall = fingerprintInquiryAPI.checkFingerprintsInquiryStatus(inquiryId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}