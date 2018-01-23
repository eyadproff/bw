package sa.gov.nic.bio.bw.client.core.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;

/**
 * Created by Fouad on 18-Jul-17.
 */
public abstract class ServiceBase implements JavaDelegate
{
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
	
	protected void bypassSuccessResponseWithNoResult(DelegateExecution execution, boolean sameFormOnSuccess)
	{
		execution.setVariable("successResponse", true);
		execution.setVariable("sameFormOnSuccess", sameFormOnSuccess);
	}
	
	protected void bypassErrorCode(DelegateExecution execution, String errorCode)
	{
		execution.setVariable("successResponse", false);
		execution.setVariable("sameForm", true);
		execution.setVariable("errorCode", errorCode);
	}
}