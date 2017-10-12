package sa.gov.nic.bio.bw.client.cancelcriminal.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.workflow.ServiceBase;
import sa.gov.nic.bio.bw.client.cancelcriminal.webservice.CancelCriminalAPI;

import java.util.logging.Logger;

public class CancelCriminalService extends ServiceBase
{
	private static final Logger LOGGER = Logger.getLogger(CancelCriminalService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		String personId = (String) execution.getVariable("personId");
		String criminalId = (String) execution.getVariable("criminalId");
		execution.removeVariables();
		
		LOGGER.fine("personId = " + personId);
		LOGGER.fine("criminalId = " + criminalId);
		
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		Call<Boolean> apiCall = cancelCriminalAPI.cancelCriminal(personId, criminalId);
		ApiResponse<Boolean> response = Context.getWebserviceManager().executeApi(apiCall);
		bypassResponse(execution, response, true);
	}
}