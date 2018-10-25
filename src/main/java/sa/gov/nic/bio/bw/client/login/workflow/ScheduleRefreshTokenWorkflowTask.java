package sa.gov.nic.bio.bw.client.login.workflow;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;

public class ScheduleRefreshTokenWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private String userToken;
	
	@Override
	public void execute()
	{
		Context.getUserSession().setAttribute("userToken", userToken);
		Context.getWebserviceManager().scheduleRefreshToken(userToken);
	}
}