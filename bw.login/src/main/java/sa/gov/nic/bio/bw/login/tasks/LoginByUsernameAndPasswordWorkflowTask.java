package sa.gov.nic.bio.bw.login.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.LoginBean;
import sa.gov.nic.bio.bw.login.webservice.IdentityAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class LoginByUsernameAndPasswordWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private String username;
	@Input(alwaysRequired = true) private String password;
	@Output private LoginBean loginBean;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn) throws Signal
	{
		IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
		Call<LoginBean> apiCall = identityAPI.login(username, password, AppConstants.APP_CODE,
		                                            "U"); // U = User?
		TaskResponse<LoginBean> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		loginBean = taskResponse.getResult();
	}
}