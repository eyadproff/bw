package sa.gov.nic.bio.bw.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.activiti.engine.ActivitiIllegalArgumentException;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.ConfigManager;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.CoreFxController;
import sa.gov.nic.bio.bw.client.core.beans.UserData;
import sa.gov.nic.bio.bw.client.core.utils.*;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.core.webservice.NicHijriCalendarData;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

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
	    else AppInstanceManager.registerInstance();
	
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
	    
	    String sIdleWarningBeforeSeconds = configManager.getProperty("idle.warning.before.seconds");
	    String sIdleWarningAfterSeconds = configManager.getProperty("idle.warning.after.seconds");
	    String sReadTimeoutSeconds = configManager.getProperty("webservice.readTimeoutSeconds");
	    String sConnectTimeoutSeconds = configManager.getProperty("webservice.connectTimeoutSeconds");
	
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
	
	    String webserviceBaseUrl = configManager.getProperty("webservice.baseUrl");
	    if(webserviceBaseUrl == null) LOGGER.warning("webserviceBaseUrl is null!");
	
	    // retrieve the codebase URL from the JNLP
	    try
	    {
	    	BasicService basicService = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
		    URL codebase = basicService.getCodeBase();
		    
		    webserviceBaseUrl = codebase.getHost();
		    int port = codebase.getPort();
		    if(port > 0) webserviceBaseUrl += ":" + port;
	    }
	    catch(Exception | NoClassDefFoundError e)
	    {
		    LOGGER.warning("This is not a web-start application. The default server address will be used!");
	    }
	
	    if(webserviceBaseUrl == null)
	    {
		    String errorCode = "C001-00010";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	
	    LOGGER.info("webserviceBaseUrl = " + webserviceBaseUrl);
	
	    webserviceManager.init(webserviceBaseUrl, readTimeoutSeconds, connectTimeoutSeconds);
	
	    UserData userData = new UserData();
	
	    Context.init(configManager, workflowManager, webserviceManager, executorService, scheduledExecutorService, userData);
	
	    LookupAPI lookupAPI = webserviceManager.getApi(LookupAPI.class);
	    Call<NicHijriCalendarData> apiCall = lookupAPI.lookupNicHijriCalendarData();
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
		    if(errorCode != null) notifyPreloader(new ProgressMessage(null, errorCode, String.valueOf(httpCode)));
		    else notifyPreloader(new ProgressMessage(null, "C001-00012"));
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
	    
	    windowTitle = AppUtils.replaceNumbers(title, Locale.getDefault());
	    
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
	    coreFxController.passInitialResources(labelsBundle, errorsBundle, messagesBundle, appIcon, windowTitle, idleWarningBeforeSeconds, idleWarningAfterSeconds);
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