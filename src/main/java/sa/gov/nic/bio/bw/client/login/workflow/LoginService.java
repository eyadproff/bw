package sa.gov.nic.bio.bw.client.login.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.delegate.DelegateExecution;
import retrofit2.Call;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.core.workflow.ServiceBase;
import sa.gov.nic.bio.bw.client.login.webservice.IdentityAPI;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
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
		if(Context.getRuntimeEnvironment() != null && Context.getRuntimeEnvironment() != RuntimeEnvironment.DEV)
		{
			String serverUrl = Context.getWebserviceManager().getServerUrl();
			
			boolean newUpdates = BclUtils.checkForAppUpdates(serverUrl, "bw", false, json ->
			{
				ObjectMapper mapper = new ObjectMapper();
				try
				{
					@SuppressWarnings("unchecked")
					Map<String, Map<String, String>> map = mapper.readValue(json, Map.class);
					return map;
				}
				catch(IOException e)
				{
					LOGGER.log(Level.WARNING, "Failed to parse the JSON while checking for new updates!!", e);
				}
				
				return null;
			});
			
			System.out.println("newUpdates = " + newUpdates);
			
			if(newUpdates)
			{
				String errorCode = "B001-00000";
				bypassErrorCode(execution, errorCode);
				return;
			}
		}
		
		String username = (String) execution.getVariable("username");
		String password = (String) execution.getVariable("password");
		
		LOGGER.fine("username = " + username);
		LOGGER.fine("password = " + password);
		
		String url = System.getProperty("jnlp.bio.bw.service.lookupMenusRoles");
		LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
		Call<Map<String, Set<String>>> menusRolesCall = lookupAPI.lookupMenuRoles(url, "BW");
		ApiResponse<Map<String, Set<String>>> menusRolesResponse = Context.getWebserviceManager().executeApi(menusRolesCall);
		
		Map<String, Set<String>> menusRoles;
		if(menusRolesResponse.isSuccess()) menusRoles = menusRolesResponse.getResult();
		else
		{
			bypassResponse(execution, menusRolesResponse, false);
			return;
		}
		
		IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
		url = System.getProperty("jnlp.bio.bw.service.login");
		Call<LoginBean> apiCall = identityAPI.login(url, username, password, "BW", "U"); // U = User?
		ApiResponse<LoginBean> response = Context.getWebserviceManager().executeApi(apiCall);
		
		if(response.isSuccess())
		{
			LoginBean loginBean = response.getResult();
			
			UserSession userSession = new UserSession();
			Context.setUserSession(userSession);
			
			UserInfo userInfo = loginBean.getUserInfo();
			String userToken = loginBean.getUserToken();
			
			userSession.setAttribute("userInfo", userInfo);
			userSession.setAttribute("userToken", userToken);
			userSession.setAttribute("menusRoles", menusRoles);
			
			LOGGER.info("the user (" + userInfo.getUserName() + ") is logged in");
			LOGGER.fine("userToken = " + userToken);
			Arrays.stream(AppUtils.decodeJWT(userToken)).forEach(part -> LOGGER.fine(part)); // LOGGER::fine doesn't work, I don't know WHY!!!
		}
		
		bypassResponse(execution, response, false);
	}
}