package sa.gov.nic.bio.bw.client.login.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.core.workflow.ServiceBase;
import sa.gov.nic.bio.bw.client.login.webservice.LoginAPI;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.net.SocketException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Fouad on 18-Jul-17.
 */
public class LoginService extends ServiceBase
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
		
		String url = System.getProperty("jnlp.bio.bw.service.lookupMenuRoles");
		LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
		Call<Map<String, Set<String>>> menuRolesCall = lookupAPI.lookupMenuRoles(url, "BW");
		ApiResponse<Map<String, Set<String>>> menuRolesResponse = Context.getWebserviceManager().executeApi(menuRolesCall);
		
		Map<String, Set<String>> menuRoles;
		if(menuRolesResponse.isSuccess()) menuRoles = menuRolesResponse.getResult();
		else
		{
			bypassResponse(execution, menuRolesResponse, false);
			return;
		}
		
		LoginAPI loginAPI = Context.getWebserviceManager().getApi(LoginAPI.class);
		url = System.getProperty("jnlp.bio.bw.service.login");
		Call<LoginBean> apiCall = loginAPI.login(url, username, password, machineIpAddress, "BW", "U");
		ApiResponse<LoginBean> response = Context.getWebserviceManager().executeApi(apiCall);
		
		if(response.isSuccess())
		{
			LoginBean resultBean = response.getResult();
			resultBean.setMenuRoles(menuRoles);
			LOGGER.info("the user (" + resultBean.getUserInfo().getUserName() + ") is logged in");
		}
		
		bypassResponse(execution, response, false);
	}
}