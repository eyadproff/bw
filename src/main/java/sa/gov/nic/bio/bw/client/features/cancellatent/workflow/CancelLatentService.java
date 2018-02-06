package sa.gov.nic.bio.bw.client.features.cancellatent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.cancellatent.webservice.CancelLatentAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.logging.Logger;

public class CancelLatentService
{
	private static final Logger LOGGER = Logger.getLogger(CancelLatentService.class.getName());
	
	public static ServiceResponse<Boolean> execute(long personId, String latentId)
	{
		CancelLatentAPI cancelLatentAPI = Context.getWebserviceManager().getApi(CancelLatentAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.cancelLatent");
		Call<Boolean> apiCall = cancelLatentAPI.cancelLatent(url, personId, latentId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}