package sa.gov.nic.bio.bw.client.login.workflow;

import com.google.gson.Gson;
import retrofit2.Call;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.login.utils.LoginErrorCodes;
import sa.gov.nic.bio.bw.client.login.webservice.IdentityAPI;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginService
{
	private static final Logger LOGGER = Logger.getLogger(LoginService.class.getName());
	
	public static ServiceResponse<LoginBean> execute(String username, String password)
	{
		return execute(username, password, -1, null);
	}
	
	public static ServiceResponse<LoginBean> execute(String username, int fingerPosition, String fingerprint)
	{
		return execute(username, null, fingerPosition, fingerprint);
	}
	
	public static ServiceResponse<LoginBean> execute(String username, String password, int fingerPosition,
	                                                 String fingerprint)
	{
		if(Context.getRuntimeEnvironment() != RuntimeEnvironment.LOCAL) // if not local, check for updates
		{
			// TODO: improve it
			String serverUrl = Context.getServerUrl();
			if(Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV) serverUrl = AppConstants.DEV_SERVER_URL;
			
			boolean newUpdates = BclUtils.checkForAppUpdates(serverUrl, "bw", false,
			                                                 json ->
			{
				try
				{
					@SuppressWarnings("unchecked")
					Map<String, Map<String, String>> map = new Gson().fromJson(json, Map.class);
					return map;
				}
				catch(Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed to parse the JSON while checking for new updates!!", e);
				}
				
				return null;
			});
			
			if(newUpdates)
			{
				String errorCode = LoginErrorCodes.N003_00001.getCode();
				return ServiceResponse.failure(errorCode, null, null);
			}
		}
		
		LOGGER.fine("username = " + username);
		LOGGER.fine("password = " + password);
		
		String url = System.getProperty("jnlp.bio.bw.service.lookupMenusRoles");
		LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
		Call<Map<String, Set<String>>> menusRolesCall = lookupAPI.lookupMenuRoles(url, "BW");
		ServiceResponse<Map<String, Set<String>>> menusRolesResponse = Context.getWebserviceManager()
																			  .executeApi(menusRolesCall);
		
		Map<String, Set<String>> menusRoles;
		if(menusRolesResponse.isSuccess()) menusRoles = menusRolesResponse.getResult();
		else // bypass the negative response
		{
			return ServiceResponse.failure(menusRolesResponse.getErrorCode(), menusRolesResponse.getException(),
			                               menusRolesResponse.getErrorDetails());
		}
		
		IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
		
		Call<LoginBean> apiCall;
		
		if(password != null)
		{
			url = System.getProperty("jnlp.bio.bw.service.login");
			apiCall = identityAPI.login(url, username, password, "BW", "U"); // U = User?
		}
		else
		{
			url = System.getProperty("jnlp.bio.bw.service.loginByFingerprint");
			apiCall = identityAPI.loginByFingerprint(url, username, fingerPosition, fingerprint, "BW");
		}
		
		ServiceResponse<LoginBean> response = Context.getWebserviceManager().executeApi(apiCall);
		
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
			
			// LOGGER::fine doesn't work, I don't know WHY!!!
			Arrays.stream(AppUtils.decodeJWT(userToken)).forEach(part -> LOGGER.fine(part));
			
			return ServiceResponse.success(response.getResult());
		}
		else
		{
			return ServiceResponse.failure(response.getErrorCode(), response.getException(),
			                               response.getErrorDetails());
		}
	}
}