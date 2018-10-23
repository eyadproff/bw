package sa.gov.nic.bio.bw.client.login.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.login.webservice.IdentityAPI;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;
import sa.gov.nic.bio.commons.TaskResponse;

public class LoginByUsernameAndFingerprintWorkflowTask implements WorkflowTask
{
	@Input(required = true) private String username;
	@Input(required = true) private int fingerPosition;
	@Input(required = true) private String fingerprint;
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