package sa.gov.nic.bio.bw.client.features.cancelcriminal.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice.CancelCriminalAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class CancelCriminalService
{
	public static ServiceResponse<Boolean> execute(String personId, int personIdType, String criminalId)
	{
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.cancelCriminal.byPersonId");
		Call<Boolean> apiCall = cancelCriminalAPI.cancelCriminalByPersonId(url, personId, personIdType, criminalId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
	
	public static ServiceResponse<Boolean> execute(String inquiryId, String criminalId)
	{
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.cancelCriminal.byInquiryId");
		Call<Boolean> apiCall = cancelCriminalAPI.cancelCriminalByInquiryId(url, inquiryId, criminalId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}