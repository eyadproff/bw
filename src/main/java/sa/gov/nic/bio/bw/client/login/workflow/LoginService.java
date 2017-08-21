package sa.gov.nic.bio.bw.client.login.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.login.webservice.LoginAPI;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.net.SocketException;
import java.util.logging.Logger;

/**
 * Created by Fouad on 18-Jul-17.
 */
public class LoginService implements JavaDelegate
{
	private static final Logger LOGGER = Logger.getLogger(LoginService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		String username = (String) execution.getVariable("username");
		String password = (String) execution.getVariable("password");
		
		LOGGER.fine("username = " + username);
		LOGGER.fine("password = " + password);
		
		String machineIpAddress;
		try
		{
			machineIpAddress = AppUtils.getMachineIpAddress();
		}
		catch(SocketException e)
		{
			String errorCode = "C003-00001";
			bypassErrorCode(execution, errorCode);
			return;
		}
		
		if(machineIpAddress == null)
		{
			String errorCode = "C003-00002";
			bypassErrorCode(execution, errorCode);
			return;
		}
		
		LOGGER.info("The machine IP address is " + machineIpAddress);
		
		LoginAPI loginAPI = Context.getWebserviceManager().getApi(LoginAPI.class);
		Call<LoginBean> apiCall = loginAPI.login(username, password, machineIpAddress);
		ApiResponse<LoginBean> response = Context.getWebserviceManager().executeApi(apiCall);
		bypassResponse(execution, response);
		
		if(response.isSuccess())
		{
			LoginBean resultBean = response.getResult();
			LOGGER.info("the user (" + resultBean.getUserInfo().getUserName() + ") is logged in");
		}
	}
	
	private <T> void bypassResponse(DelegateExecution execution, ApiResponse<T> response)
	{
		boolean successResponse = response.isSuccess();
		execution.setVariable("successResponse", successResponse);
		
		if(successResponse)
		{
			T resultBean = response.getResult();
			execution.setVariable("resultBean", resultBean);
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
	
	private void bypassErrorCode(DelegateExecution execution, String errorCode)
	{
		execution.setVariable("successResponse", false);
		execution.setVariable("sameForm", true);
		execution.setVariable("errorCode", errorCode);
	}
}