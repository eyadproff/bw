package sa.gov.nic.bio.bw.client.features.cancellatent.workflow;

import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class CancelLatentService
{
	public static ServiceResponse<Boolean> execute(long personId, String latentId)
	{
		return null;
		/*CancelLatentAPI cancelLatentAPI = Context.getWebserviceManager().getApi(CancelLatentAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.cancelLatent");
		Call<Boolean> apiCall = cancelLatentAPI.cancelLatent(url, personId, latentId);
		return Context.getWebserviceManager().executeApi(apiCall);*/
	}
}