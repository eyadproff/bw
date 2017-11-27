package sa.gov.nic.bio.bw.client.login.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.login.webservice.LogoutAPI;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Fouad on 18-Jul-17.
 */
public class LogoutService implements JavaDelegate
{
	private static final Logger LOGGER = Logger.getLogger(LogoutService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		String username = Context.getUserData().getLoginBean().getUserInfo().getUserName();
		String token = Context.getUserData().getLoginBean().getUserToken();
		
		LOGGER.fine("token = " + token);
		
		// we don't care about logout response, so just make it asynchronous
		Context.getExecutorService().execute(() ->
		{
			LogoutAPI logoutAPI = Context.getWebserviceManager().getApi(LogoutAPI.class);
			String url = System.getProperty("jnlp.bio.bw.service.logout");
			Call<Void> apiCall = logoutAPI.logout(url, token);
			ApiResponse<Void> response = Context.getWebserviceManager().executeApi(apiCall);
			
			if(response.getHttpCode() != 200)
			{
				int httpCode = response.getHttpCode();
				String errorCode = response.getErrorCode();
				Exception exception = response.getException();
				
				LOGGER.log(Level.WARNING, "logout webservice failed (httpCode = " + httpCode + ", errorCode = " + errorCode + ")", exception);
			}
			
			Context.deleteUserData();
		});
		
		LOGGER.info("the user (" + username + ") is logged out");
	}
}