package sa.gov.nic.bio.bw.client.core;

import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.ConfigManager;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class Context
{
	private static final Context INSTANCE = new Context();
	
	private RuntimeEnvironment runtimeEnvironment;
	private ConfigManager configManager;
	private WorkflowManager2 workflowManager;
	private WebserviceManager webserviceManager;
	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;
	private UserSession userSession;
	
	private Context(){}
	
	public static void init(RuntimeEnvironment runtimeEnvironment, ConfigManager configManager, WorkflowManager2 workflowManager, WebserviceManager webserviceManager, ExecutorService executorService, ScheduledExecutorService scheduledExecutorService, UserSession userSession)
	{
		INSTANCE.runtimeEnvironment = runtimeEnvironment;
		INSTANCE.configManager = configManager;
		INSTANCE.workflowManager = workflowManager;
		INSTANCE.webserviceManager = webserviceManager;
		INSTANCE.executorService = executorService;
		INSTANCE.scheduledExecutorService = scheduledExecutorService;
		INSTANCE.userSession = userSession;
	}
	
	public static RuntimeEnvironment getRuntimeEnvironment(){return INSTANCE.runtimeEnvironment;}
	public static ConfigManager getConfigManager(){return INSTANCE.configManager;}
	public static WorkflowManager2 getWorkflowManager(){return INSTANCE.workflowManager;}
	public static WebserviceManager getWebserviceManager(){return INSTANCE.webserviceManager;}
	public static ExecutorService getExecutorService(){return INSTANCE.executorService;}
	public static ScheduledExecutorService getScheduledExecutorService(){return INSTANCE.scheduledExecutorService;}
	
	public static void setUserSession(UserSession userSession){INSTANCE.userSession = userSession;}
	public static UserSession getUserSession(){return INSTANCE.userSession;}
}