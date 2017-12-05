package sa.gov.nic.bio.bw.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.activiti.engine.ActivitiIllegalArgumentException;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.utils.ConfigManager;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.CoreFxController;
import sa.gov.nic.bio.bw.client.core.beans.UserData;
import sa.gov.nic.bio.bw.client.core.utils.*;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.*;
import java.util.logging.Formatter;

public class AppEntryPoint extends Application
{
	private static final Logger LOGGER = Logger.getLogger(AppEntryPoint.class.getName());
	
	// initial resources
	private ConfigManager configManager = new ConfigManager();
	private WorkflowManager workflowManager = new WorkflowManager();
	private WebserviceManager webserviceManager = new WebserviceManager();
	private ExecutorService executorService = Executors.newWorkStealingPool();
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	
	private ResourceBundle labelsBundle;
	private ResourceBundle errorsBundle;
	private ResourceBundle messagesBundle;
	private ResourceBundle topMenusBundle;
	
	private Image appIcon;
	private URL fxmlUrl;
	private String windowTitle;
	
	private boolean successfulInit = false;
	private GuiLanguage initialLanguage = GuiLanguage.ARABIC; // TODO: make it configurable from properties file
	
	// default values
	private int idleWarningBeforeSeconds = 480; // 8 minutes
	private int idleWarningAfterSeconds = 120; // 2 minutes
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
	
	    List<String> workflowFilePaths;
	
	    try
	    {
		    workflowFilePaths = AppUtils.listResourceFiles(getClass().getProtectionDomain(), ".*\\.bpmn20.xml$");
	    }
	    catch(Exception e)
	    {
		    String errorCode = "C001-00008";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    try
	    {
		    workflowManager.load(workflowFilePaths);
	    }
	    catch(ActivitiIllegalArgumentException e)
	    {
		    String errorCode = "C001-00009";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	    
	    String webserviceBaseUrl = System.getProperty("bio.serverUrl");
	    String runtimeEnvironment = System.getProperty("jnlp.bio.runtime.environment"); // PROD, INT, DEV
	    
	    
	    if(webserviceBaseUrl == null) // usually local run
	    {
		    LOGGER.warning("webserviceBaseUrl is null! We will use the default one.");
		    webserviceBaseUrl = configManager.getProperty("webservice.baseUrl");
		    runtimeEnvironment = "DEV";
		
		    // populate the JNLP properties to the system properties
		    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("sa/gov/nic/bio/bw/client/core/config/jnlp.properties");
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
	    LOGGER.info("runtimeEnvironment = " + runtimeEnvironment);
	
	    String sIdleWarningBeforeSeconds = System.getProperty("jnlp.bio.bw.idle.warning.before.seconds");
	    String sIdleWarningAfterSeconds = System.getProperty("jnlp.bio.bw.idle.warning.after.seconds");
	    String sReadTimeoutSeconds = System.getProperty("jnlp.bio.bw.webservice.readTimeoutSeconds");
	    String sConnectTimeoutSeconds = System.getProperty("jnlp.bio.bw.webservice.connectTimeoutSeconds");
	
	    if(sIdleWarningBeforeSeconds == null)
	    {
		    LOGGER.warning("idleWarningBeforeSeconds is null! Default value is " + idleWarningBeforeSeconds);
	    }
	    else try
	    {
		    idleWarningBeforeSeconds = Integer.parseInt(sIdleWarningBeforeSeconds);
		    LOGGER.info("idleWarningBeforeSeconds = " + idleWarningBeforeSeconds);
	    }
	    catch(NumberFormatException e)
	    {
		    LOGGER.warning("Failed to parse sIdleWarningBeforeSeconds as int! sIdleWarningBeforeSeconds = " + sIdleWarningBeforeSeconds);
	    }
	
	    if(sIdleWarningAfterSeconds == null)
	    {
		    LOGGER.warning("sIdleWarningAfterSeconds is null! Default value is " + idleWarningAfterSeconds);
	    }
	    else try
	    {
		    idleWarningAfterSeconds = Integer.parseInt(sIdleWarningAfterSeconds);
		    LOGGER.info("idleWarningAfterSeconds = " + idleWarningAfterSeconds);
	    }
	    catch(NumberFormatException e)
	    {
		    LOGGER.warning("Failed to parse sIdleWarningAfterSeconds as int! sIdleWarningAfterSeconds = " + sIdleWarningAfterSeconds);
	    }
	
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
		    LOGGER.warning("Failed to parse sReadTimeoutSeconds as int! sReadTimeoutSeconds = " + sReadTimeoutSeconds);
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
		    LOGGER.warning("Failed to parse sConnectTimeoutSeconds as int! sConnectTimeoutSeconds = " + sConnectTimeoutSeconds);
	    }
	
	    webserviceManager.init(webserviceBaseUrl, readTimeoutSeconds, connectTimeoutSeconds);
	
	    UserData userData = new UserData();
	    LOGGER.fine("HELLO WORLD");
	
	    Context.init(configManager, workflowManager, webserviceManager, executorService, scheduledExecutorService, userData);
	
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
	    	int httpCode = apiResponse.getHttpCode();
		    String errorCode = apiResponse.getErrorCode();
		    if(errorCode != null) notifyPreloader(new ProgressMessage(apiResponse.getException(), errorCode, String.valueOf(httpCode)));
		    else notifyPreloader(new ProgressMessage(apiResponse.getException(), "C001-00012"));
		    return;
	    }
	    
	    try
	    {
		    labelsBundle = ResourceBundle.getBundle(CoreFxController.RB_LABELS_FILE, initialLanguage.getLocale(), new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00013";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    try
	    {
		    errorsBundle = ResourceBundle.getBundle(CoreFxController.RB_ERRORS_FILE, initialLanguage.getLocale(), new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00014";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    try
	    {
		    messagesBundle = ResourceBundle.getBundle(CoreFxController.RB_MESSAGES_FILE, initialLanguage.getLocale(), new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00015";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    try
	    {
		    topMenusBundle = ResourceBundle.getBundle(CoreFxController.RB_TOP_MENUS_FILE, initialLanguage.getLocale(), new UTF8Control());
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "C001-00021";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    InputStream appIconStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CoreFxController.APP_ICON_FILE);
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
		    title = labelsBundle.getString("window.title") + " " + version;
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
	    coreFxController.passInitialResources(labelsBundle, errorsBundle, messagesBundle, topMenusBundle, appIcon, windowTitle, idleWarningBeforeSeconds, idleWarningAfterSeconds);
	    coreFxController.getGuiState().setLanguage(initialLanguage);
	    
	    primaryStage.getScene().setNodeOrientation(initialLanguage.getNodeOrientation());
	    primaryStage.centerOnScreen();
	    primaryStage.setOnCloseRequest(event -> LOGGER.info("The main window is closed"));
	
	    notifyPreloader(ProgressMessage.SUCCESSFULLY_DONE);
	
	    primaryStage.setOnCloseRequest(event ->
        {
        	Context.getExecutorService().shutdownNow();
        	Context.getScheduledExecutorService().shutdownNow();
        	Platform.exit();
        });
	    primaryStage.show();
	    LOGGER.info("The main window is shown");
	
	    // set minimum dimensions for the stage
	    primaryStage.setMinWidth(primaryStage.getWidth());
	    primaryStage.setMinHeight(primaryStage.getHeight());
	
	    LOGGER.exiting(AppEntryPoint.class.getName(), "start(Stage ignoredStage)");
    }
    
    public static void main(String[] args) // not invoked in webstart!
    {
	    LOGGER.entering(AppEntryPoint.class.getName(), "main(String[] args)");
	    launch(args);
	    LOGGER.exiting(AppEntryPoint.class.getName(), "main(String[] args)");
    }
}