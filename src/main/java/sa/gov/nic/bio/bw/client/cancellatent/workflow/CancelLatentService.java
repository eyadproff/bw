package sa.gov.nic.bio.bw.client.cancellatent.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.cancellatent.webservice.CancelLatentAPI;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.workflow.ServiceBase;

import java.util.logging.Logger;

public class CancelLatentService  extends ServiceBase
{
	private static final Logger LOGGER = Logger.getLogger(CancelLatentService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		String personId = (String) execution.getVariable("personId");
		String latentId = (String) execution.getVariable("latentId");
		execution.removeVariables();
		
		LOGGER.fine("personId = " + personId);
		LOGGER.fine("latentId = " + latentId);
		
		CancelLatentAPI cancelLatentAPI = Context.getWebserviceManager().getApi(CancelLatentAPI.class);
		Call<Boolean> apiCall = cancelLatentAPI.cancelLatent(personId, latentId);
		ApiResponse<Boolean> response = Context.getWebserviceManager().executeApi(apiCall);
		bypassResponse(execution, response, true);
	}
}