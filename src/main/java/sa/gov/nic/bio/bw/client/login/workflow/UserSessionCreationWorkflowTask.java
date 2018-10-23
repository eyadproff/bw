package sa.gov.nic.bio.bw.client.login.workflow;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;

public class UserSessionCreationWorkflowTask implements WorkflowTask
{
	@Override
	public void execute()
	{
		Context.setUserSession(new UserSession());
	}
}