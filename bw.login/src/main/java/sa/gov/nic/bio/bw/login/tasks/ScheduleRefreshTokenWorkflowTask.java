package sa.gov.nic.bio.bw.login.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

public class ScheduleRefreshTokenWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private String userToken;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn)
	{
		Context.getUserSession().setAttribute("userToken", userToken);
		Context.getWebserviceManager().scheduleRefreshToken(userToken);
	}
}