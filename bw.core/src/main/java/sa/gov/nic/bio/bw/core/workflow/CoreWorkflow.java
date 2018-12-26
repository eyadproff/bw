package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.tasks.LogoutTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class CoreWorkflow extends WorkflowBase
{
	private boolean loggedIn = false;
	
	@Override
	public void onProcess(Map<String, String> configurations) throws InterruptedException
	{
		ServiceLoader<Workflow> serviceLoader = ServiceLoader.load(Workflow.class);
		List<Workflow> workflows = new ArrayList<>();
		serviceLoader.iterator().forEachRemaining(workflows::add);
		Map<String, List<Workflow>> workflowMap = workflows.stream().collect(Collectors.groupingBy(Workflow::getId));
		
		while(true)
		{
			try
			{
				if(loggedIn) throw new Signal(SignalType.SUCCESS_LOGIN);
				
				Workflow loginWorkflow = workflowMap.get(KEY_WORKFLOW_LOGIN).get(0);
				Context.getWorkflowManager().setCurrentWorkflow(loginWorkflow);
				loginWorkflow.onProcess(null);
			}
			catch(Signal loginSignal)
			{
				Context.getWorkflowManager().getUserTasks().clear();
				SignalType loginSignalType = loginSignal.getSignalType();
				
				switch(loginSignalType)
				{
					case SUCCESS_LOGIN:
					{
						boolean showErrorOnHome = loggedIn;
						loggedIn = true;
						
						Map<String, String> homeConfigurations = new HashMap<>();
						homeConfigurations.put("showErrorOnHome", String.valueOf(showErrorOnHome));
						
						try
						{
							Workflow homeWorkflow = workflowMap.get(KEY_WORKFLOW_HOME).get(0);
							Context.getWorkflowManager().setCurrentWorkflow(homeWorkflow);
							homeWorkflow.onProcess(homeConfigurations);
						}
						catch(Signal homeSignal)
						{
							Context.getWorkflowManager().getUserTasks().clear();
							SignalType homeSignalType = homeSignal.getSignalType();
							
							switch(homeSignalType)
							{
								case LOGOUT:
								{
									loggedIn = false;
									Context.getExecutorService().submit(new LogoutTask());
									Context.getWorkflowManager().setCurrentWorkflow(null);
									break;
								}
								case INVALID_STATE:
								{
									handleInvalidStateSignal(homeSignal.getPayload());
									Context.getWorkflowManager().setCurrentWorkflow(null);
									break;
								}
								default: // wrong signal
								{
									LOGGER.severe("homeSignalType = " + homeSignalType);
									Context.getWorkflowManager().setCurrentWorkflow(null);
								}
							}
						}
						break;
					}
					case INVALID_STATE:
					{
						handleInvalidStateSignal(loginSignal.getPayload());
						Context.getWorkflowManager().setCurrentWorkflow(null);
						break;
					}
					default: // wrong signal
					{
						LOGGER.severe("loginSignalType = " + loginSignalType);
						Context.getWorkflowManager().setCurrentWorkflow(null);
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