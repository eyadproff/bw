package sa.gov.nic.bio.bw.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.stage.Stage;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.CoreFxController;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.CombinedResourceBundle;
import sa.gov.nic.bio.bw.client.core.utils.ConfigManager;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.PreloaderNotification;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.core.webservice.NicHijriCalendarData;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;
import sa.gov.nic.bio.bw.client.preloader.utils.StartupErrorCodes;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 */
public class AppEntryPoint extends Application
{
	public static GuiLanguage guiLanguage = GuiLanguage.ARABIC; // default value (changed in AppPreloader class)
	
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
	
	private ResourceBundle stringsBundle;
	private ResourceBundle topMenusBundle;
	
	private URL fxmlUrl;
	private String windowTitle;
	
	private boolean successfulInit = false;
	
	private int readTimeoutSeconds = 60; // 1 minute
	private int connectTimeoutSeconds = 60; // 1 minute
    
    @Override
    public void init()
    {
	    LOGGER.entering(AppEntryPoint.class.getName(), "init()");
	
	    try
	    {
		    configManager.load();
	    }
	    catch(IOException e)
	    {
	    	String errorCode = StartupErrorCodes.C001_00004.getCode();
	    	String[] errorDetails = {"Failed to load the config file!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    String webserviceBaseUrl = System.getProperty("bio.serverUrl");
	    String sRuntimeEnvironment = System.getProperty("jnlp.bio.runtime.environment", "LOCAL");
	    LOGGER.info("sRuntimeEnvironment = " + sRuntimeEnvironment);
	
	    RuntimeEnvironment runtimeEnvironment = RuntimeEnvironment.byName(sRuntimeEnvironment);
	    
	    if(runtimeEnvironment == RuntimeEnvironment.LOCAL)
	    {
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
	
	    List<String> errorBundleNames;
	
	    try
	    {
		    errorBundleNames = AppUtils.listResourceFiles(getClass().getProtectionDomain(),
		                                                  ".*/errors.properties$",
		                                                  true, runtimeEnvironment);
	    }
	    catch(Exception e)
	    {
		    String errorCode = StartupErrorCodes.C001_00005.getCode();
		    String[] errorDetails = {"Failed to load the error bundles!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    ResourceBundle errorsBundle = new CombinedResourceBundle(errorBundleNames, guiLanguage.getLocale(),
	                                                             new UTF8Control());
	    ((CombinedResourceBundle) errorsBundle).load();
	    
	    try
	    {
		    stringsBundle = ResourceBundle.getBundle(CoreFxController.RB_LABELS_FILE, guiLanguage.getLocale(),
		                                             new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = StartupErrorCodes.C001_00006.getCode();
		    String[] errorDetails = {"Core \"stringsBundle\" resource bundle is missing!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    try
	    {
		    topMenusBundle = ResourceBundle.getBundle(CoreFxController.RB_TOP_MENUS_FILE, guiLanguage.getLocale(),
		                                              new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = StartupErrorCodes.C001_00007.getCode();
		    String[] errorDetails = {"Core \"topMenusBundle\" resource bundle is missing!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    fxmlUrl = Thread.currentThread().getContextClassLoader().getResource(CoreFxController.FXML_FILE);
	    if(fxmlUrl == null)
	    {
		    String errorCode = StartupErrorCodes.C001_00008.getCode();
		    String[] errorDetails = {"Core \"fxmlUrl\" is null!"};
		    notifyPreloader(PreloaderNotification.failure(null, errorCode, errorDetails));
		    return;
	    }
	
	    String[] urls = configManager.getProperty("dev.webservice.urls").split("[,\\s]+");
	    
	    if(runtimeEnvironment == RuntimeEnvironment.LOCAL || runtimeEnvironment == RuntimeEnvironment.DEV)
	    {
		
		    CountDownLatch latch = new CountDownLatch(1);
		    AtomicReference<String> reference = new AtomicReference<>();
		
		    Platform.runLater(() ->
		    {
			    String dialogTitle = stringsBundle.getString("dialog.choice.title");
			    String headerText = stringsBundle.getString("dialog.choice.selectServer.headerText");
			    String buttonText = stringsBundle.getString("dialog.choice.selectServer.button");
			
			    reference.set(DialogUtils.showChoiceDialog(null, null, dialogTitle,
			                                  headerText, urls, buttonText, true,
			                                  guiLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT));
			    latch.countDown();
		    });
		
		    try
		    {
			    latch.await(); // wait until the user choose a url
			    webserviceBaseUrl = reference.get();
		    }
		    catch(InterruptedException e)
		    {
			    e.printStackTrace();
		    }
		
	    }
	
	    if(webserviceBaseUrl == null)
	    {
		    String errorCode = StartupErrorCodes.C001_00009.getCode();
		    String[] errorDetails = {"\"webserviceBaseUrl\" is null!"};
		    notifyPreloader(PreloaderNotification.failure(null, errorCode, errorDetails));
		    return;
	    }
	
	    LOGGER.info("webserviceBaseUrl = " + webserviceBaseUrl);
	
	    webserviceManager.init(webserviceBaseUrl, readTimeoutSeconds, connectTimeoutSeconds);
	
	    Context.attach(runtimeEnvironment, configManager, workflowManager, webserviceManager, executorService,
	                   scheduledExecutorService, errorsBundle, new UserSession());
	
	    LookupAPI lookupAPI = webserviceManager.getApi(LookupAPI.class);
	    String url = System.getProperty("jnlp.bio.bw.service.lookupNicHijriCalendarData");
	    Call<NicHijriCalendarData> apiCall = lookupAPI.lookupNicHijriCalendarData(url);
	    ServiceResponse<NicHijriCalendarData> webServiceResponse = webserviceManager.executeApi(apiCall);
	
	    if(webServiceResponse.isSuccess())
	    {
		    NicHijriCalendarData nicHijriCalendarData = webServiceResponse.getResult();
		    try
		    {
			    AppUtils.injectNicHijriCalendarData(nicHijriCalendarData);
		    }
		    catch(Exception e)
		    {
			    String errorCode = StartupErrorCodes.C001_00010.getCode();
			    String[] errorDetails = {"Failed to inject NicHijriCalendarData!"};
			    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
			    return;
		    }
	    }
	    else
	    {
		    Exception exception = webServiceResponse.getException();
		    String errorCode = webServiceResponse.getErrorCode();
		    String[] errorDetails = webServiceResponse.getErrorDetails();
		    notifyPreloader(PreloaderNotification.failure(exception, errorCode, errorDetails));
		    return;
	    }
	
	    String version = configManager.getProperty("app.version");
	
	    if(version == null)
	    {
		    String errorCode = StartupErrorCodes.C001_00011.getCode();
		    String[] errorDetails = {"Config \"app.version\" is missing!"};
		    notifyPreloader(PreloaderNotification.failure(null, errorCode, errorDetails));
		    return;
	    }
	
	    String title;
	    try
	    {
		    title = stringsBundle.getString("window.title") + " " + version + " (" +
				    stringsBundle.getString("label.environment." +
						                            Context.getRuntimeEnvironment().name().toLowerCase()) + ")";
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = StartupErrorCodes.C001_00012.getCode();
		    String[] errorDetails = {"Label text \"window.title\" is missing!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
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
	
	    FXMLLoader coreStageLoader = new FXMLLoader(fxmlUrl, stringsBundle);
	
	    Stage primaryStage;
	    try
	    {
		    primaryStage = coreStageLoader.load();
	    }
	    catch(IOException e)
	    {
		    String errorCode = StartupErrorCodes.C001_00013.getCode();
		    String[] errorDetails = {"Failed to load the core FXML correctly!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	    
	    CoreFxController coreFxController = coreStageLoader.getController();
	    coreFxController.passInitialResources(stringsBundle, topMenusBundle, windowTitle, guiLanguage);
	    
	    primaryStage.getScene().setNodeOrientation(guiLanguage.getNodeOrientation());
	    primaryStage.centerOnScreen();
	    primaryStage.setOnCloseRequest(GuiUtils.createOnExitHandler(primaryStage, coreFxController));
	
	    notifyPreloader(PreloaderNotification.success());
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