package sa.gov.nic.bio.bw.client.login.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.login.tasks.LogoutTask;

/**
 * Created by Fouad on 18-Jul-17.
 */
public class LogoutService implements JavaDelegate
{
	@Override
	public void execute(DelegateExecution execution)
	{
		// we don't care about logout response, so just make it asynchronous
		Context.getExecutorService().execute(new LogoutTask());
	}
}