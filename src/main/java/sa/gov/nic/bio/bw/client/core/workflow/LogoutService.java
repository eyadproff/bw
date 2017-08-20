package sa.gov.nic.bio.bw.client.core.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.webservice.LogoutAPI;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Fouad on 18-Jul-17.
 */
public class LogoutService implements JavaDelegate
{
	private static final Logger LOGGER = Logger.getLogger(LogoutService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		String token = (String) execution.getVariable("token");
		
		LOGGER.fine("token = " + token);
		
		LogoutAPI logoutAPI = Context.getWebserviceManager().getApi(LogoutAPI.class);
		try
		{
			logoutAPI.logout(token).execute();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		LOGGER.info("the user is logged out");
	}
}