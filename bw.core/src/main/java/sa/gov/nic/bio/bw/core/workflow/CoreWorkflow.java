package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.webservice.LogoutTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class CoreWorkflow extends WorkflowBase
{
	@Override
	public void onProcess() throws InterruptedException
	{
		ServiceLoader<Workflow> serviceLoader = ServiceLoader.load(Workflow.class);
		List<Workflow> workflows = new ArrayList<>();
		serviceLoader.iterator().forEachRemaining(workflows::add);
		Map<String, List<Workflow>> workflowMap = workflows.stream().collect(Collectors.groupingBy(Workflow::getId));
		
		while(true)
		{
			try
			{
				workflowMap.get(KEY_WORKFLOW_LOGIN).get(0).onProcess();
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
							workflowMap.get(KEY_WORKFLOW_HOME).get(0).onProcess();
						}
						catch(Signal homeSignal)
						{
							SignalType homeSignalType = homeSignal.getSignalType();
							
							switch(homeSignalType)
							{
								case LOGOUT:
								{
									Context.getExecutorService().submit(new LogoutTask());
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