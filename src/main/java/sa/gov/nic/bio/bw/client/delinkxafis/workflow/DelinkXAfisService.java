package sa.gov.nic.bio.bw.client.delinkxafis.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.workflow.ServiceBase;
import sa.gov.nic.bio.bw.client.delinkxafis.webservice.DelinkXAfisAPI;

import java.util.logging.Logger;

public class DelinkXAfisService extends ServiceBase
{
	private static final Logger LOGGER = Logger.getLogger(DelinkXAfisService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		String personId = (String) execution.getVariable("personId");
		String criminalId = (String) execution.getVariable("criminalId");
		execution.removeVariables();
		
		LOGGER.fine("personId = " + personId);
		LOGGER.fine("criminalId = " + criminalId);
		
		DelinkXAfisAPI delinkXAfisAPI = Context.getWebserviceManager().getApi(DelinkXAfisAPI.class);
		Call<Boolean> apiCall = delinkXAfisAPI.delinkXAfis(personId, criminalId);
		ApiResponse<Boolean> response = Context.getWebserviceManager().executeApi(apiCall);
		bypassResponse(execution, response, true);
	}
}