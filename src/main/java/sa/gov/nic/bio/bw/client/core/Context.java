package sa.gov.nic.bio.bw.client.core;

import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;

import java.util.concurrent.ExecutorService;

public class Context
{
	private static final Context INSTANCE = new Context();
	
	private ConfigManager configManager;
	private WorkflowManager workflowManager;
	private WebserviceManager webserviceManager;
	private ExecutorService executorService;
	
	public static void init(ConfigManager configManager, WorkflowManager workflowManager, WebserviceManager webserviceManager, ExecutorService executorService)
	{
		INSTANCE.configManager = configManager;
		INSTANCE.workflowManager = workflowManager;
		INSTANCE.webserviceManager = webserviceManager;
		INSTANCE.executorService = executorService;
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
}