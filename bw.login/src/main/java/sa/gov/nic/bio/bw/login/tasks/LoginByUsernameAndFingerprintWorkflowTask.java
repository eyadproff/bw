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

public class LoginByUsernameAndFingerprintWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private String username;
	@Input(alwaysRequired = true) private int fingerPosition;
	@Input(alwaysRequired = true) private String fingerprint;
	@Output private LoginBean loginBean;
	
	@Override
	public void execute() throws Signal
	{
		IdentityAPI identityAPI = Context.getWebserviceManager().getApi(IdentityAPI.class);
		Call<LoginBean> apiCall = identityAPI.loginByFingerprint(username, fingerPosition, fingerprint,
		                                                         AppConstants.APP_CODE);
		TaskResponse<LoginBean> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		loginBean = taskResponse.getResult();
	}
}