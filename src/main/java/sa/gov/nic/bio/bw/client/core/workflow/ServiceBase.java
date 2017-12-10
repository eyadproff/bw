package sa.gov.nic.bio.bw.client.core.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;

import java.util.logging.Logger;

/**
 * Created by Fouad on 18-Jul-17.
 */
public abstract class ServiceBase implements JavaDelegate
{
	private static final Logger LOGGER = Logger.getLogger(ServiceBase.class.getName());
	
	protected <T> void bypassResponse(DelegateExecution execution, ApiResponse<T> response, boolean sameFormOnSuccess)
	{
		boolean successResponse = response.isSuccess();
		execution.setVariable("successResponse", successResponse);
		
		if(successResponse)
		{
			T resultBean = response.getResult();
			execution.setVariable("resultBean", resultBean);
			execution.setVariable("sameForm", sameFormOnSuccess);
		}
		else
		{
			String apiUrl = response.getApiUrl();
			String errorCode = response.getErrorCode();
			int httpCode = response.getHttpCode();
			Exception exception = response.getException();
			
			execution.setVariable("apiUrl", apiUrl);
			execution.setVariable("httpCode", httpCode);
			execution.setVariable("exception", exception);
			
			bypassErrorCode(execution, errorCode);
		}
	}
	
	protected void bypassErrorCode(DelegateExecution execution, String errorCode)
	{
		execution.setVariable("successResponse", false);
		execution.setVariable("sameForm", true);
		execution.setVariable("errorCode", errorCode);
	}
}