package sa.gov.nic.bio.bw.client.core;

import sa.gov.nic.bio.bw.client.core.beans.UserData;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class Context
{
	private static final Context INSTANCE = new Context();
	
	private ConfigManager configManager;
	private WorkflowManager workflowManager;
	private WebserviceManager webserviceManager;
	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;
	private UserData userData; // TODO: temp
	
	public static void init(ConfigManager configManager, WorkflowManager workflowManager, WebserviceManager webserviceManager, ExecutorService executorService, ScheduledExecutorService scheduledExecutorService, UserData userData)
	{
		INSTANCE.configManager = configManager;
		INSTANCE.workflowManager = workflowManager;
		INSTANCE.webserviceManager = webserviceManager;
		INSTANCE.executorService = executorService;
		INSTANCE.scheduledExecutorService = scheduledExecutorService;
		INSTANCE.userData = userData;
	}
	
	public static ConfigManager getConfigManager()
	{
		return INSTANCE.configManager;
	}
	
	public static WorkflowManager getWorkflowManager()
	{
		return INSTANCE.workflowManager;
	}
	
	public static WebserviceManager getWebserviceManager()
	{
		return INSTANCE.webserviceManager;
	}
	
	public static ExecutorService getExecutorService()
	{
		return INSTANCE.executorService;
	}
	
	public static ScheduledExecutorService getScheduledExecutorService()
	{
		return INSTANCE.scheduledExecutorService;
	}
	
	public static UserData getUserData()
	{
		return INSTANCE.userData;
	}
}