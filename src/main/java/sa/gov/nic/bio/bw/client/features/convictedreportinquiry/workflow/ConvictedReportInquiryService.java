package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice.CancelCriminalAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class ConvictedReportInquiryService
{
	public static ServiceResponse<Boolean> execute(long personId, int personIdType, long criminalId)
	{
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		Call<Boolean> apiCall = cancelCriminalAPI.cancelCriminalByPersonId(personId, personIdType, criminalId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
	
	public static ServiceResponse<Boolean> execute(long inquiryId, long criminalId)
	{
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		Call<Boolean> apiCall = cancelCriminalAPI.cancelCriminalByInquiryId(inquiryId, criminalId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}