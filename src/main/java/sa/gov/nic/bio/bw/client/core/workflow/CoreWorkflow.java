package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.home.workflow.HomeWorkflow;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;
import sa.gov.nic.bio.bw.client.login.workflow.LoginWorkflow;
import sa.gov.nic.bio.bw.client.login.workflow.LogoutWorkflow;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class CoreWorkflow extends WorkflowBase<Void, Void>
{
	private static final Logger LOGGER = Logger.getLogger(CoreWorkflow.class.getName());
	
	public CoreWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		while(true)
		{
			LoginBean loginBean = new LoginWorkflow(formRenderer, userTasks).onProcess(null);
			
			if(loginBean == null)
			{
				LOGGER.severe("The login workflow returns null!");
				continue;
			}
			
			try
			{
				new HomeWorkflow(formRenderer, userTasks).onProcess(loginBean);
			}
			catch(Signal signal)
			{
				SignalType signalType = signal.getSignalType();
				
				switch(signalType)
				{
					case LOGOUT:
					{
						new LogoutWorkflow(formRenderer, userTasks).onProcess(null);
						break;
					}
					default: // shouldn't happen
					{
					
					}
				}
			}
		}
	}
}