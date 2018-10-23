package sa.gov.nic.bio.bw.client.features.cancelcriminal.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice.CancelCriminalAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class CancelCriminalService
{
	public static TaskResponse<Boolean> execute(long personId, int samisIdType, long criminalId)
	{
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		Call<Boolean> apiCall = cancelCriminalAPI.cancelCriminalByPersonId(personId, samisIdType, criminalId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
	
	public static TaskResponse<Boolean> execute(long inquiryId, long criminalId)
	{
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		Call<Boolean> apiCall = cancelCriminalAPI.cancelCriminalByInquiryId(inquiryId, criminalId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}