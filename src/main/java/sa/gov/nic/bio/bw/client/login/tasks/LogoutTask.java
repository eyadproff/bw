package sa.gov.nic.bio.bw.client.login.tasks;

import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.login.webservice.IdentityAPI;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogoutTask extends Task<Void>
{
	private static final Logger LOGGER = Logger.getLogger(LogoutTask.class.getName());
	
	@Override
	protected Void call()
	{
		UserSession userSession = Context.getUserSession();
		if(userSession == null) return null; // user is logged in
		
		UserInfo userInfo = (UserInfo) userSession.getAttribute("userInfo");
		String username = userInfo.getUserName();
		String userToken = (String) userSession.getAttribute("userToken");
		
		Context.setUserSession(null);
		
		IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.logout");
		Call<Void> apiCall = identityAPI.logout(url, userToken);
		ApiResponse<Void> response = Context.getWebserviceManager().executeApi(apiCall);
		
		if(response.getHttpCode() != 200)
		{
			int httpCode = response.getHttpCode();
			String errorCode = response.getErrorCode();
			Exception exception = response.getException();
			
			LOGGER.log(Level.WARNING, "logout webservice failed (httpCode = " + httpCode + ", errorCode = " + errorCode + ")", exception);
		}
		
		LOGGER.info("the user (" + username + ") is logged out");
		LOGGER.fine("userToken = " + userToken);
		Arrays.stream(AppUtils.decodeJWT(userToken)).forEach(part -> LOGGER.fine(part)); // LOGGER::fine doesn't work, I don't know WHY!!!
		
		return null;
	}
}