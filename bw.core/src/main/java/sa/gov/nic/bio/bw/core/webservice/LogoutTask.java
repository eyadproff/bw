package sa.gov.nic.bio.bw.core.webservice;

import javafx.application.Platform;
import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Arrays;
import java.util.logging.Level;

public class LogoutTask extends Task<Void> implements AppLogger
{
	@Override
	protected Void call()
	{
		UserSession userSession = Context.getUserSession();
		if(userSession == null) return null; // user is logged in
		
		UserInfo userInfo = (UserInfo) userSession.getAttribute("userInfo");
		String username = userInfo.getUserName();
		String userToken = (String) userSession.getAttribute("userToken");
		
		Context.setUserSession(null);
		Platform.runLater(() -> Context.getCoreFxController().getHeaderPaneController().setAvatarImage(null));
		
		LogoutAPI logoutAPI = Context.getWebserviceManager().getApi(LogoutAPI.class);
		Call<Void> apiCall = logoutAPI.logout(userToken);
		TaskResponse<Void> response = Context.getWebserviceManager().executeApi(apiCall);
		
		if(!response.isSuccess())
		{
			Exception exception = response.getException();
			String errorMessage = "Failure on logout! errorCode = " + response.getErrorCode() +
					", errorDetails = " + Arrays.toString(response.getErrorDetails());
			
			if(exception != null) LOGGER.log(Level.WARNING, errorMessage, exception);
			else LOGGER.warning(errorMessage);
		}
		
		LOGGER.info("the user (" + username + ") is logged out");
		LOGGER.fine("userToken = " + userToken);
		
		// LOGGER::fine doesn't work, I don't know WHY!!!
		Arrays.stream(AppUtils.decodeJWT(userToken)).forEach(part -> LOGGER.fine(part));
		
		return null;
	}
}