package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.tasks.LogoutTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class CoreWorkflow extends WorkflowBase
{
	private boolean loggedIn = false;
	
	public void setLoggedIn(boolean loggedIn)
	{
		this.loggedIn = loggedIn;
	}
	
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
				loginWorkflow.setTabIndex(getTabIndex());
				Context.getWorkflowManager().setCurrentWorkflow(loginWorkflow, getTabIndex());
				
				loginWorkflow.onProcess(null);
			}
			catch(Signal loginSignal)
			{
				Context.getWorkflowManager().getUserTasks(getTabIndex()).clear();
				SignalType loginSignalType = loginSignal.getSignalType();
				
				switch(loginSignalType)
				{
					case SUCCESS_LOGIN:
					{
						loggedIn = true;
						
						try
						{
							Workflow homeWorkflow = workflowMap.get(KEY_WORKFLOW_HOME).get(0);
							homeWorkflow.setTabIndex(getTabIndex());
							Context.getWorkflowManager().setCurrentWorkflow(homeWorkflow, getTabIndex());
							homeWorkflow.onProcess(null);
						}
						catch(Signal homeSignal)
						{
							if(homeSignal.getSignalType() == SignalType.EXIT_WORKFLOW) return;
							
							Context.getWorkflowManager().getUserTasks(getTabIndex()).clear();
							SignalType homeSignalType = homeSignal.getSignalType();
							
							switch(homeSignalType)
							{
								case LOGOUT:
								{
									loggedIn = false;
									Context.getExecutorService().submit(new LogoutTask());
									Context.getWorkflowManager().setCurrentWorkflow(null, getTabIndex());
									break;
								}
								case INVALID_STATE:
								{
									handleInvalidStateSignal(homeSignal.getPayload(), getTabIndex());
									Context.getWorkflowManager().setCurrentWorkflow(null, getTabIndex());
									break;
								}
								default: // wrong signal
								{
									LOGGER.severe("homeSignalType = " + homeSignalType);
									Context.getWorkflowManager().setCurrentWorkflow(null, getTabIndex());
								}
							}
						}
						break;
					}
					case INVALID_STATE:
					{
						handleInvalidStateSignal(loginSignal.getPayload(), getTabIndex());
						Context.getWorkflowManager().setCurrentWorkflow(null, getTabIndex());
						break;
					}
					default: // wrong signal
					{
						LOGGER.severe("loginSignalType = " + loginSignalType);
						Context.getWorkflowManager().setCurrentWorkflow(null, getTabIndex());
					}
				}
				
			}
		}
	}
	
	private static void handleInvalidStateSignal(Map<String, Object> payload, int tabIndex)
	{
		String errorCode = (String) payload.get(KEY_ERROR_CODE);
		Exception exception = (Exception) payload.get(KEY_EXCEPTION);
		String[] errorDetails = (String[]) payload.get(KEY_ERROR_DETAILS);
		Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, tabIndex);
	}
}