package sa.gov.nic.bio.bw.login.tasks;

import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.login.webservice.IdentityAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class ChangePasswordTask extends Task<TaskResponse<Boolean>>
{
	private String username;
	private String oldPassword;
	private String newPassword;
	
	public ChangePasswordTask(String username, String oldPassword, String newPassword)
	{
		this.username = username;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
	
	@Override
	protected TaskResponse<Boolean> call()
	{
		IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
		Call<Boolean> apiCall = identityAPI.changePassword(username, oldPassword, newPassword, AppConstants.APP_CODE,
		                                                   "U"); // U = User?
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}