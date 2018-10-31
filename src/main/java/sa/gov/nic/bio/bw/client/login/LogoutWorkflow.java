package sa.gov.nic.bio.bw.client.login;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.login.tasks.LogoutTask;

public class LogoutWorkflow extends WorkflowBase<Void, Void>
{
	@Override
	public void onProcess()
	{
		// we don't care about logout response, so just make it asynchronous
		Context.getExecutorService().submit(new LogoutTask());
	}
}