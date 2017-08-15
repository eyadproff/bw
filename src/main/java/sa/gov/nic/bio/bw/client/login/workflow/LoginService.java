package sa.gov.nic.bio.bw.client.login.workflow;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import retrofit2.Call;
import retrofit2.Response;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.login.webservice.LoginAPI;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
			String errorCode = "E02-1000";
			bypassFailureResponse(execution, errorCode);
			return;
		}
		
		if(machineIpAddress == null)
		{
			String errorCode = "E02-1001";
			bypassFailureResponse(execution, errorCode);
			return;
		}
		
		LOGGER.info("The machine IP address is " + machineIpAddress);
		
		LoginAPI loginAPI = Context.getWebserviceManager().getApi(LoginAPI.class);
		Call<LoginBean> responseCall = loginAPI.login(username, password, machineIpAddress);
		Response<LoginBean> response;
		try
		{
			response = responseCall.execute();
		}
		catch(SocketTimeoutException e)
		{
			String errorCode = "E02-1002";
			bypassFailureResponse(execution, errorCode);
			return;
		}
		catch(IOException e)
		{
			String errorCode = "E02-1003";
			bypassFailureResponse(execution, errorCode);
			return;
		}
		
		int httpCode = response.code();
		LOGGER.info("service = \"loginAPI.login\", response = " + httpCode);
		
		if(httpCode == 401) // wrong username/password
		{
			String businessErrorCode = "B02-1000";
			execution.setVariable("businessErrorCode", businessErrorCode);
			execution.setVariable("keepSameForm", true);
			throw new BpmnError(businessErrorCode);
		}
		
		if(httpCode == 404) // api not found?
		{
			// TODO: handle this
		}
		
		if(httpCode == 500) // server error
		{
			// TODO: handle this
		}
		
		LoginBean loginBean = response.body();
		
		if(loginBean == null)
		{
			String errorCode = "E02-1004";
			bypassFailureResponse(execution, errorCode);
			return;
		}
		
		String accessToken = response.headers().get("AUTH");
		LOGGER.fine("accessToken = " + accessToken);
		
		bypassSuccessResponse(execution, loginBean);
	}
	
	private void bypassSuccessResponse(DelegateExecution execution, LoginBean loginBean)
	{
		execution.setVariableLocal("successfulLogin", true);
		execution.setVariable("loginBean", loginBean);
	}
	
	private void bypassFailureResponse(DelegateExecution execution, String errorCode)
	{
		execution.setVariableLocal("successfulLogin", false);
		execution.setVariableLocal("sameForm", true);
		execution.setVariableLocal("errorCode", errorCode);
	}
}