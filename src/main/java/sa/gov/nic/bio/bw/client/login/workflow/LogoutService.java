package sa.gov.nic.bio.bw.client.login.workflow;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.login.tasks.LogoutTask;

/**
 * Created by Fouad on 18-Jul-17.
 */
public class LogoutService
{
	public static void execute()
	{
		// we don't care about logout response, so just make it asynchronous
		Context.getExecutorService().execute(new LogoutTask());
	}
}