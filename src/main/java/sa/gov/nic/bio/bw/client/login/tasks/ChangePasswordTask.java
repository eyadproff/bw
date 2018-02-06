package sa.gov.nic.bio.bw.client.login.tasks;

import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.login.webservice.IdentityAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class ChangePasswordTask extends Task<ServiceResponse<Boolean>>
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
	protected ServiceResponse<Boolean> call()
	{
		IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.changePassword");
		Call<Boolean> apiCall = identityAPI.changePassword(url, username, oldPassword, newPassword, "BW",
		                                                   "U"); // U = User?
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}