package sa.gov.nic.bio.bw.client.features.cancellatent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.cancellatent.webservice.CancelLatentAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class CancelLatentService
{
	public static TaskResponse<Boolean> execute(long personId, String latentId)
	{
		CancelLatentAPI cancelLatentAPI = Context.getWebserviceManager().getApi(CancelLatentAPI.class);
		Call<Boolean> apiCall = cancelLatentAPI.cancelLatent(personId, latentId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}