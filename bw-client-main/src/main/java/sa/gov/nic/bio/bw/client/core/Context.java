package sa.gov.nic.bio.bw.client.core;

import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;

public class Context
{
	private static final Context INSTANCE = new Context();
	
	private ConfigManager configManager;
	private WorkflowManager workflowManager;
	private WebserviceManager webserviceManager;
	
	public static void setManagers(ConfigManager configManager, WorkflowManager workflowManager, WebserviceManager webserviceManager)
	{
		INSTANCE.configManager = configManager;
		INSTANCE.workflowManager = workflowManager;
		INSTANCE.webserviceManager = webserviceManager;
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
}