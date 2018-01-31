package sa.gov.nic.bio.bw.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.CoreFxController;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppInstanceManager;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.CombinedResourceBundle;
import sa.gov.nic.bio.bw.client.core.utils.ConfigManager;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.ProgressMessage;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.core.webservice.NicHijriCalendarData;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * The entry point of the application. Since Java 8, the main method is not required as long as the entry point
 * class extends <code>javafx.application.Application</code>. We kept the main method for old IDEs that don't
 * support this behavior.
 *
 * When the app preloader shows the splash screen, <code>AppEntryPoint.init()</code> starts. It is responsible for
 * the real initialization of the application. Whenever an error occurs, it notifies the app preloader with the error
 * details to show it on the dialog. When <code>init()</code> finishes, <code>AppEntryPoint.start()</code> starts on
 * the UI thread. <code>start()</code> is responsible for creating the primary stage. Once the primary stage is
 * ready, it notifies the app preloader with a success message in order to close the splash screen. After that,
 * the primary stage is shown.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public class AppEntryPoint extends Application
{
	private static final Logger LOGGER = Logger.getLogger(AppEntryPoint.class.getName());
	
	private static final ThreadFactory DAEMON_THREAD_FACTORY = runnable ->
	{
		Thread thread = Executors.defaultThreadFactory().newThread(runnable);
		thread.setDaemon(true);
		return thread;
	};
	
	// initial resources
	private ConfigManager configManager = new ConfigManager();
	private WorkflowManager workflowManager = new WorkflowManager();
	private WebserviceManager webserviceManager = new WebserviceManager();
	private ExecutorService executorService = Executors.newWorkStealingPool();
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
																								DAEMON_THREAD_FACTORY);
	
	private ResourceBundle errorsBundle;
	private ResourceBundle labelsBundle;
	private ResourceBundle messagesBundle;
	private ResourceBundle topMenusBundle;
	
	private Image appIcon;
	private URL fxmlUrl;
	private String windowTitle;
	
	private boolean successfulInit = false;
	private GuiLanguage initialLanguage;
	
	private int readTimeoutSeconds = 60; // 1 minute
	private int connectTimeoutSeconds = 60; // 1 minute
    
    @Override
    public void init()
    {
	    LOGGER.entering(AppEntryPoint.class.getName(), "init()");
	
	    if(AppInstanceManager.checkIfAlreadyRunning())
	    {
		    String errorCode = "C001-00006";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	
	    try
	    {
		    configManager.load();
	    }
	    catch(IOException e)
	    {
		    String errorCode = "C001-00007";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    String webserviceBaseUrl = System.getProperty("bio.serverUrl");
	    String sRuntimeEnvironment = System.getProperty("jnlp.bio.runtime.environment"); // PROD, INT, DEV
	    
	    if(webserviceBaseUrl == null) // usually local run
	    {
		    LOGGER.warning("webserviceBaseUrl is null! We will use the default one.");
		    webserviceBaseUrl = configManager.getProperty("dev.webservice.baseUrl");
		    sRuntimeEnvironment = "DEV";
		
		    // populate the JNLP properties to the system properties
		    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
		    		                                    "sa/gov/nic/bio/bw/client/core/config/jnlp.properties");
		    InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		    Properties properties = new Properties();
		    try
		    {
			    properties.load(isr);
			    isr.close();
			    LOGGER.info("Populate jnlp.properties to System properties.");
			    for(Object key : Collections.list(properties.propertyNames()))
			    {
				    String sKey = (String) key;
				    String value = properties.getProperty(sKey);
				    LOGGER.info(sKey + " = " + value);
				    System.setProperty(sKey, value);
			    }
		    }
		    catch(IOException e1)
		    {
			    LOGGER.log(Level.SEVERE, "Failed to load the file jnlp.properties!", e1);
		    }
	    }
	    
	    if(webserviceBaseUrl == null)
	    {
		    String errorCode = "C001-00010";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	
	    LOGGER.info("webserviceBaseUrl = " + webserviceBaseUrl);
	    LOGGER.info("sRuntimeEnvironment = " + sRuntimeEnvironment);
	
	    RuntimeEnvironment runtimeEnvironment = RuntimeEnvironment.byName(sRuntimeEnvironment);
	
	    String sReadTimeoutSeconds = System.getProperty("jnlp.bio.bw.webservice.readTimeoutSeconds");
	    String sConnectTimeoutSeconds = System.getProperty("jnlp.bio.bw.webservice.connectTimeoutSeconds");
	
	    if(sReadTimeoutSeconds == null)
	    {
		    LOGGER.warning("sReadTimeoutSeconds is null! Default value is " + readTimeoutSeconds);
	    }
	    else try
	    {
		    readTimeoutSeconds = Integer.parseInt(sReadTimeoutSeconds);
		    LOGGER.info("readTimeoutSeconds = " + readTimeoutSeconds);
	    }
	    catch(NumberFormatException e)
	    {
		    LOGGER.warning("Failed to parse sReadTimeoutSeconds as int! sReadTimeoutSeconds = " +
				           sReadTimeoutSeconds);
	    }
	
	    if(sConnectTimeoutSeconds == null)
	    {
		    LOGGER.warning("sConnectTimeoutSeconds is null! Default value is " + connectTimeoutSeconds);
	    }
	    else try
	    {
		    connectTimeoutSeconds = Integer.parseInt(sConnectTimeoutSeconds);
		    LOGGER.info("connectTimeoutSeconds = " + connectTimeoutSeconds);
	    }
	    catch(NumberFormatException e)
	    {
		    LOGGER.warning("Failed to parse sConnectTimeoutSeconds as int! sConnectTimeoutSeconds = " +
				           sConnectTimeoutSeconds);
	    }
	
	    webserviceManager.init(webserviceBaseUrl, readTimeoutSeconds, connectTimeoutSeconds);
	    
	    Context.init(runtimeEnvironment, configManager, workflowManager, webserviceManager, executorService,
	                 scheduledExecutorService, new UserSession());
	    
	    LookupAPI lookupAPI = webserviceManager.getApi(LookupAPI.class);
	    String url = System.getProperty("jnlp.bio.bw.service.lookupNicHijriCalendarData");
	    Call<NicHijriCalendarData> apiCall = lookupAPI.lookupNicHijriCalendarData(url);
	    ApiResponse<NicHijriCalendarData> apiResponse = webserviceManager.executeApi(apiCall);
	
	    if(apiResponse.isSuccess())
	    {
		    NicHijriCalendarData nicHijriCalendarData = apiResponse.getResult();
		    try
		    {
			    AppUtils.injectNicHijriCalendarData(nicHijriCalendarData);
		    }
		    catch(Exception e)
		    {
			    String errorCode = "C001-00011";
			    notifyPreloader(new ProgressMessage(e, errorCode));
			    return;
		    }
	    }
	    else
	    {
		    String apiUrl = apiResponse.getApiUrl();
	    	int httpCode = apiResponse.getHttpCode();
		    String errorCode = apiResponse.getErrorCode();
		    if(errorCode != null)
		    {
		    	if(httpCode > 0) notifyPreloader(new ProgressMessage(apiResponse.getException(), errorCode, apiUrl,
			                                                         String.valueOf(httpCode)));
		    	else notifyPreloader(new ProgressMessage(apiResponse.getException(), errorCode, apiUrl));
		    }
		    else
	        {
	        	if(httpCode > 0) notifyPreloader(new ProgressMessage(apiResponse.getException(), "C001-00012",
		                                                             String.valueOf(httpCode)));
	        	else notifyPreloader(new ProgressMessage(apiResponse.getException(), "C001-00012"));
		    }
		    return;
	    }
	    
	    // set the default language
	    Preferences prefs = Preferences.userNodeForPackage(AppConstants.PREF_NODE_CLASS);
	    String userLanguage = prefs.get(AppConstants.UI_LANGUAGE_PREF_NAME, null);
	    if(userLanguage == null)
	    {
	    	userLanguage = System.getProperty("user.language","en"); // Use the OS default language
	    }
	    
	    if("ar".equalsIgnoreCase(userLanguage)) initialLanguage = GuiLanguage.ARABIC;
	    else initialLanguage = GuiLanguage.ENGLISH;
	    Locale.setDefault(initialLanguage.getLocale());
	    
	    List<String> errorBundleNames;
	
	    try
	    {
		    errorBundleNames = AppUtils.listResourceFiles(getClass().getProtectionDomain(),
		                                                  ".*/errors.properties$",
		                                                  true, runtimeEnvironment);
	    }
	    catch(Exception e)
	    {
		    String errorCode = "C001-00008";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	    
	    errorsBundle = new CombinedResourceBundle(errorBundleNames, initialLanguage.getLocale(), new UTF8Control());
	    ((CombinedResourceBundle) errorsBundle).load();
	    
	    try
	    {
		    labelsBundle = ResourceBundle.getBundle(CoreFxController.RB_LABELS_FILE, initialLanguage.getLocale(),
		                                            new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00013";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    /*try
	    {
		    errorsBundle = ResourceBundle.getBundle(CoreFxController.RB_ERRORS_FILE, initialLanguage.getLocale(),
		                                            new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00014";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }*/
	
	    try
	    {
		    messagesBundle = ResourceBundle.getBundle(CoreFxController.RB_MESSAGES_FILE, initialLanguage.getLocale(),
		                                              new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00015";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    try
	    {
		    topMenusBundle = ResourceBundle.getBundle(CoreFxController.RB_TOP_MENUS_FILE, initialLanguage.getLocale(),
		                                              new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00021";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    InputStream appIconStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
	    		                    CoreFxController.APP_ICON_FILE);
	    if(appIconStream == null)
	    {
		    String errorCode = "C001-00016";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	    appIcon = new Image(appIconStream);
	
	    fxmlUrl = Thread.currentThread().getContextClassLoader().getResource(CoreFxController.FXML_FILE);
	    if(fxmlUrl == null)
	    {
		    String errorCode = "C001-00017";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	
	    String version = configManager.getProperty("app.version");
	    
	    if(version == null)
	    {
		    String errorCode = "C001-00018";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	
	    String title;
	    try
	    {
		    title = labelsBundle.getString("window.title") + " " + version + " (" +
				    labelsBundle.getString("label.environment." +
						                   Context.getRuntimeEnvironment().name().toLowerCase()) + ")";
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00019";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	    
	    windowTitle = AppUtils.replaceNumbersOnly(title, Locale.getDefault());
	    
	    successfulInit = true;
	    LOGGER.exiting(AppEntryPoint.class.getName(), "init()");
    }
    
    @Override
    public void start(Stage ignoredStage)
    {
	    LOGGER.entering(AppEntryPoint.class.getName(), "start(Stage ignoredStage)");
	    
	    if(!successfulInit) return;
	
	    FXMLLoader coreStageLoader = new FXMLLoader(fxmlUrl, labelsBundle);
	
	    Stage primaryStage;
	    try
	    {
		    primaryStage = coreStageLoader.load();
	    }
	    catch(IOException e)
	    {
		    String errorCode = "C001-00020";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	    
	    CoreFxController coreFxController = coreStageLoader.getController();
	    coreFxController.passInitialResources(labelsBundle, errorsBundle, messagesBundle, topMenusBundle, appIcon,
	                                          windowTitle, initialLanguage);
	    
	    primaryStage.getScene().setNodeOrientation(initialLanguage.getNodeOrientation());
	    primaryStage.centerOnScreen();
	    primaryStage.setOnCloseRequest(GuiUtils.createOnExitHandler(primaryStage, coreFxController));
	
	    notifyPreloader(ProgressMessage.SUCCESSFULLY_DONE);
	    primaryStage.show();
	    LOGGER.info("The main window is shown");
	
	    // set minimum dimensions for the stage
	    primaryStage.setMinWidth(primaryStage.getWidth());
	    primaryStage.setMinHeight(primaryStage.getHeight());
	
	    LOGGER.exiting(AppEntryPoint.class.getName(), "start(Stage ignoredStage)");
    }
    
    public static void main(String[] args) // optional
    {
	    LOGGER.entering(AppEntryPoint.class.getName(), "main(String[] args)");
	    launch(args);
	    LOGGER.exiting(AppEntryPoint.class.getName(), "main(String[] args)");
    }
}