package sa.gov.nic.bio.bw.login.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

public class UserSessionCreationWorkflowTask implements WorkflowTask
{
	@Override
	public void execute(Integer workflowId, Long workflowTcn)
	{
		Context.setUserSession(new UserSession());
	}
}