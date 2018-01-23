package sa.gov.nic.bio.bw.client.login.tasks;

import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.login.webservice.IdentityAPI;

import java.util.logging.Logger;

public class ChangePasswordTask extends Task<Boolean>
{
	private static final Logger LOGGER = Logger.getLogger(ChangePasswordTask.class.getName());
	
	private String username;
	private String oldPassword;
	private String newPassword;
	
	private String apiUrl;
	private String errorCode;
	private int httpCode;
	
	public ChangePasswordTask(String username, String oldPassword, String newPassword)
	{
		this.username = username;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
	
	public String getApiUrl()
	{
		return apiUrl;
	}
	
	public String getErrorCode()
	{
		return errorCode;
	}
	
	public int getHttpCode()
	{
		return httpCode;
	}
	
	@Override
	protected Boolean call() throws Exception
	{
		IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.changePassword");
		Call<Boolean> apiCall = identityAPI.changePassword(url, username, oldPassword, newPassword, "BW", "U"); // U = User?
		ApiResponse<Boolean> response = Context.getWebserviceManager().executeApi(apiCall);
		apiUrl = response.getApiUrl();
		
		if(response.isSuccess())
		{
			Boolean result = response.getResult();
			return result != null && result;
		}
		else
		{
			errorCode = response.getErrorCode();
			httpCode = response.getHttpCode();
			throw response.getException();
		}
	}
}