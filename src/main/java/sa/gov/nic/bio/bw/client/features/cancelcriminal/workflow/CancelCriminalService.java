package sa.gov.nic.bio.bw.client.features.cancelcriminal.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice.CancelCriminalAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.logging.Logger;

public class CancelCriminalService
{
	private static final Logger LOGGER = Logger.getLogger(CancelCriminalService.class.getName());
	
	public static ServiceResponse<Boolean> execute(String personId, String criminalId)
	{
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.cancelCriminal");
		Call<Boolean> apiCall = cancelCriminalAPI.cancelCriminal(url, personId, criminalId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}