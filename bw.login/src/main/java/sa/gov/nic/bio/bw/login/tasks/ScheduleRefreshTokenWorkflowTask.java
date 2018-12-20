package sa.gov.nic.bio.bw.login.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

public class ScheduleRefreshTokenWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private String userToken;
	
	@Override
	public void execute()
	{
		Context.getUserSession().setAttribute("userToken", userToken);
		Context.getWebserviceManager().scheduleRefreshToken(userToken);
	}
}