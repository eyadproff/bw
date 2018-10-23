package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.home.HomeWorkflow;
import sa.gov.nic.bio.bw.client.login.LoginWorkflow;
import sa.gov.nic.bio.bw.client.login.LogoutWorkflow;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CoreWorkflow extends WorkflowBase<Void, Void>
{
	CoreWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException
	{
		while(true)
		{
			try
			{
				new LoginWorkflow(formRenderer, userTasks).onProcess(null);
			}
			catch(Signal loginSignal)
			{
				SignalType loginSignalType = loginSignal.getSignalType();
				
				switch(loginSignalType)
				{
					case SUCCESS_LOGIN:
					{
						try
						{
							new HomeWorkflow(formRenderer, userTasks).onProcess(null);
						}
						catch(Signal homeSignal)
						{
							SignalType homeSignalType = homeSignal.getSignalType();
							
							switch(homeSignalType)
							{
								case LOGOUT:
								{
									new LogoutWorkflow(formRenderer, userTasks).onProcess(null);
									break;
								}
								case INVALID_STATE:
								{
									handleInvalidStateSignal(homeSignal.getPayload());
									break;
								}
								default: // wrong signal
								{
									LOGGER.severe("homeSignalType = " + homeSignalType);
								}
							}
						}
						break;
					}
					case INVALID_STATE:
					{
						handleInvalidStateSignal(loginSignal.getPayload());
						break;
					}
					default: // wrong signal
					{
						LOGGER.severe("loginSignalType = " + loginSignalType);
					}
				}
				
			}
		}
	}
	
	private static void handleInvalidStateSignal(Map<String, Object> payload)
	{
		String errorCode = (String) payload.get(KEY_ERROR_CODE);
		Exception exception = (Exception) payload.get(KEY_EXCEPTION);
		String[] errorDetails = (String[]) payload.get(KEY_ERROR_DETAILS);
		Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
	}
}