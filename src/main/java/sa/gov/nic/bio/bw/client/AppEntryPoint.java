package sa.gov.nic.bio.bw.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.activiti.engine.ActivitiIllegalArgumentException;
import retrofit2.Call;
import retrofit2.Response;
import sa.gov.nic.bio.bw.client.core.ConfigManager;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.CoreFxController;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.ProgressMessage;
import sa.gov.nic.bio.bw.client.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.core.webservice.NicHijriCalendarData;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class AppEntryPoint extends Application
{
	private static final Logger LOGGER = Logger.getLogger(AppEntryPoint.class.getName());
	
	// initial resources
	private ConfigManager configManager = new ConfigManager();
	private WorkflowManager workflowManager = new WorkflowManager();
	private WebserviceManager webserviceManager = new WebserviceManager();
	private ExecutorService executorService = Executors.newWorkStealingPool();
	
	private ResourceBundle labelsBundle;
	private ResourceBundle errorsBundle;
	private ResourceBundle messagesBundle;
	
	private Image appIcon;
	private URL fxmlUrl;
	
	private boolean successfulInit = false;
	private GuiLanguage initialLanguage = GuiLanguage.ARABIC; // TODO: make it configurable from properties file
    
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
		    String errorCode = "E00-1005";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    List<String> workflowFilePaths;
	
	    try
	    {
		    workflowFilePaths = AppUtils.listResourceFiles(getClass().getProtectionDomain(), ".bpmn20.xml");
	    }
	    catch(Exception e)
	    {
		    String errorCode = "E00-1006";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	    
	    try
	    {
		    workflowManager.load(workflowFilePaths);
	    }
	    catch(ActivitiIllegalArgumentException e)
	    {
		    String errorCode = "E00-1007";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    String webserviceBaseUrl = configManager.getProperty("webservice.baseUrl");
	    if(webserviceBaseUrl == null) LOGGER.warning("webserviceBaseUrl is null!");
	
	    try
	    {
		    Method lookupMethod = Class.forName("javax.jnlp.ServiceManager").getMethod("lookup", String.class);
		    Object basicService = lookupMethod.invoke(null, "javax.jnlp.BasicService");
		    Method getCodeBaseMethod = Class.forName("javax.jnlp.BasicService").getMethod("getCodeBase");
		    URL codebase = (URL) getCodeBaseMethod.invoke(basicService);
		    webserviceBaseUrl = codebase.getHost();
	    }
	    catch(Exception e)
	    {
		    LOGGER.warning("This is not a web-start application. The default server address will be used!");
	    }
	
	    if(webserviceBaseUrl == null)
	    {
		    String errorCode = "E00-1008";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	    
	    LOGGER.info("webserviceBaseUrl = " + webserviceBaseUrl);
	
	    webserviceManager.init(webserviceBaseUrl);
	
	    Context.init(configManager, workflowManager, webserviceManager, executorService);
	
	    LookupAPI lookupAPI = webserviceManager.getApi(LookupAPI.class);
	    Call<NicHijriCalendarData> hijriCalendarDataLookupBeanCall = lookupAPI.lookupNicHijriCalendarData();
	    Response<NicHijriCalendarData> response;
	    try
	    {
		    response = hijriCalendarDataLookupBeanCall.execute();
	    }
	    catch(SocketTimeoutException e)
	    {
		    e.printStackTrace();
		    // TODO: report error
		    return;
	    }
	    catch(IOException e)
	    {
	    	e.printStackTrace();
		    // TODO: report error
		    return;
	    }
	
	    int httpCode = response.code();
	    
	    if(httpCode == 200)
	    {
		    NicHijriCalendarData nicHijriCalendarData = response.body();
		    try
		    {
			    AppUtils.injectNicHijriCalendarData(nicHijriCalendarData);
		    }
		    catch(Exception e)
		    {
			    e.printStackTrace();
			    // TODO: report warning
		    }
	    }
	    else
	    {
		    System.out.println("httpCode = " + httpCode);
		    // TODO: report error
		    return;
	    }
	
	    try
	    {
		    labelsBundle = AppUtils.getResourceBundle(CoreFxController.RB_LABELS_FILE, initialLanguage);
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "E00-1009";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    try
	    {
		    errorsBundle = AppUtils.getResourceBundle(CoreFxController.RB_ERRORS_FILE, initialLanguage);
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "E00-1010";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    try
	    {
		    messagesBundle = AppUtils.getResourceBundle(CoreFxController.RB_MESSAGES_FILE, initialLanguage);
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = "E00-1011";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	
	    InputStream appIconStream = AppUtils.getResourceAsStream(CoreFxController.APP_ICON_FILE);
	    if(appIconStream == null)
	    {
		    String errorCode = "E00-1012";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	    appIcon = new Image(appIconStream);
	
	    fxmlUrl = AppUtils.getResourceURL(CoreFxController.FXML_FILE);
	    if(fxmlUrl == null)
	    {
		    String errorCode = "E00-1013";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	    
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
		    String errorCode = "E00-1014";
		    notifyPreloader(new ProgressMessage(e, errorCode));
		    return;
	    }
	    
	    CoreFxController coreFxController = coreStageLoader.getController();
	    coreFxController.passInitialResources(labelsBundle, errorsBundle, messagesBundle, appIcon);
	    coreFxController.getGuiState().setLanguage(initialLanguage);
	    
	    primaryStage.getScene().setNodeOrientation(initialLanguage.getNodeOrientation());
	    primaryStage.centerOnScreen();
	    primaryStage.setOnCloseRequest(event -> LOGGER.info("The main window is closed"));
	
	    notifyPreloader(ProgressMessage.SUCCESSFULLY_DONE);
	
	    primaryStage.setOnCloseRequest(event -> System.exit(0)); // TODO: temporary
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