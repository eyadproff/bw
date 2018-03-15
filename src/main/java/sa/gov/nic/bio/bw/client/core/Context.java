package sa.gov.nic.bio.bw.client.core;

import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.client.core.utils.ConfigManager;
import sa.gov.nic.bio.bw.client.core.utils.FxClassLoader;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class Context
{
	private static final Context INSTANCE = new Context();
	
	private RuntimeEnvironment runtimeEnvironment;
	private ConfigManager configManager;
	private WorkflowManager workflowManager;
	private WebserviceManager webserviceManager;
	private BioKitManager bioKitManager;
	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;
	private ResourceBundle errorsBundle;
	private UserSession userSession;
	private String serverUrl;
	private CoreFxController coreFxController;
	private FxClassLoader fxClassLoader;
	
	private Context(){}
	
	public static void attach(RuntimeEnvironment runtimeEnvironment, ConfigManager configManager,
	                          WorkflowManager workflowManager, WebserviceManager webserviceManager,
	                          BioKitManager bioKitManager, ExecutorService executorService,
	                          ScheduledExecutorService scheduledExecutorService, ResourceBundle errorsBundle,
	                          UserSession userSession, String serverUrl)
	{
		INSTANCE.runtimeEnvironment = runtimeEnvironment;
		INSTANCE.configManager = configManager;
		INSTANCE.workflowManager = workflowManager;
		INSTANCE.webserviceManager = webserviceManager;
		INSTANCE.bioKitManager = bioKitManager;
		INSTANCE.executorService = executorService;
		INSTANCE.scheduledExecutorService = scheduledExecutorService;
		INSTANCE.errorsBundle = errorsBundle;
		INSTANCE.userSession = userSession;
		INSTANCE.serverUrl = serverUrl;
	}
	
	public static RuntimeEnvironment getRuntimeEnvironment(){return INSTANCE.runtimeEnvironment;}
	public static ConfigManager getConfigManager(){return INSTANCE.configManager;}
	public static WorkflowManager getWorkflowManager(){return INSTANCE.workflowManager;}
	public static WebserviceManager getWebserviceManager(){return INSTANCE.webserviceManager;}
	public static BioKitManager getBioKitManager(){return INSTANCE.bioKitManager;}
	public static ExecutorService getExecutorService(){return INSTANCE.executorService;}
	public static ScheduledExecutorService getScheduledExecutorService(){return INSTANCE.scheduledExecutorService;}
	
	public static void setErrorsBundle(ResourceBundle errorsBundle){INSTANCE.errorsBundle = errorsBundle;}
	public static ResourceBundle getErrorsBundle(){return INSTANCE.errorsBundle;}
	
	public static void setUserSession(UserSession userSession){INSTANCE.userSession = userSession;}
	public static UserSession getUserSession(){return INSTANCE.userSession;}
	
	public static String getServerUrl(){return INSTANCE.serverUrl;}
	
	public static CoreFxController getCoreFxController(){return INSTANCE.coreFxController;}
	public static void setCoreFxController(CoreFxController coreFxController)
																	{INSTANCE.coreFxController = coreFxController;}
	
	public static FxClassLoader getFxClassLoader(){return INSTANCE.fxClassLoader;}
	public static void setFxClassLoader(FxClassLoader fxClassLoader){INSTANCE.fxClassLoader = fxClassLoader;}
}