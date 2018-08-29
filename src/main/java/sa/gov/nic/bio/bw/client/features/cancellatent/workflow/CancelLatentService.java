package sa.gov.nic.bio.bw.client.features.cancellatent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.cancellatent.webservice.CancelLatentAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class CancelLatentService
{
	public static ServiceResponse<Boolean> execute(long personId, String latentId)
	{
		CancelLatentAPI cancelLatentAPI = Context.getWebserviceManager().getApi(CancelLatentAPI.class);
		Call<Boolean> apiCall = cancelLatentAPI.cancelLatent(personId, latentId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}