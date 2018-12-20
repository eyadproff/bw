package sa.gov.nic.bio.bw.login.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

public class UserSessionCreationWorkflowTask extends WorkflowTask
{
	@Override
	public void execute()
	{
		Context.setUserSession(new UserSession());
	}
}