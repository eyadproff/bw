package sa.gov.nic.bio.bw.core;

import sa.gov.nic.bio.bw.core.beans.MenuItem;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.core.controllers.CoreFxController;
import sa.gov.nic.bio.bw.core.utils.ConfigManager;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.core.workflow.ResourceBundleProvider;
import sa.gov.nic.bio.bw.core.workflow.WorkflowManager;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class Context
{
	private static final Context INSTANCE = new Context();
	
	private String appVersion;
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
	private GuiLanguage guiLanguage;
	private Map<String, MenuItem> topMenus;
	private List<MenuItem> subMenus;
	private Map<String, ResourceBundleProvider> moduleResourceBundleProviders;
	
	private Context(){}
	
	public static void attachAppVersion(String appVersion)
	{
		INSTANCE.appVersion = appVersion;
	}
	
	public static void attach(RuntimeEnvironment runtimeEnvironment, ConfigManager configManager,
	                          WorkflowManager workflowManager, WebserviceManager webserviceManager,
	                          BioKitManager bioKitManager, ExecutorService executorService,
	                          ScheduledExecutorService scheduledExecutorService, ResourceBundle errorsBundle,
	                          UserSession userSession, String serverUrl, Map<String, MenuItem> topMenus,
	                          List<MenuItem> subMenus,
	                          Map<String, ResourceBundleProvider> moduleResourceBundleProviders)
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
		INSTANCE.topMenus = topMenus;
		INSTANCE.subMenus = subMenus;
		INSTANCE.moduleResourceBundleProviders = moduleResourceBundleProviders;
	}
	
	public static String getAppVersion(){return INSTANCE.appVersion;}
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
	
	public static GuiLanguage getGuiLanguage(){return INSTANCE.guiLanguage;}
	public static void setGuiLanguage(GuiLanguage guiLanguage){INSTANCE.guiLanguage = guiLanguage;}
	
	public static Map<String, MenuItem> getTopMenus(){return INSTANCE.topMenus;}
	public static List<MenuItem> getSubMenus(){return INSTANCE.subMenus;}
	public static Map<String, ResourceBundleProvider> getModuleResourceBundleProviders()
																	{return INSTANCE.moduleResourceBundleProviders;}
}