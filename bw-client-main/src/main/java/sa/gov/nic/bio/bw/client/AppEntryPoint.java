package sa.gov.nic.bio.bw.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sa.gov.nic.bio.bw.client.core.ConfigManager;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.CoreFxController;
import sa.gov.nic.bio.bw.client.core.ProgressMessage;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class AppEntryPoint extends Application
{
	private static final Logger LOGGER = LogManager.getLogger();
	
	// initial resources
	private ConfigManager configManager = new ConfigManager();
	private WorkflowManager workflowManager = new WorkflowManager();
	private WebserviceManager webserviceManager = new WebserviceManager();
	
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
	    LOGGER.traceEntry();
	    
	    // TEMPORARY: simulate long processing
	    try
	    {
		    notifyPreloader(new ProgressMessage(0.0, "Loading module 1"));
		    Thread.sleep(200);
		    notifyPreloader(new ProgressMessage(0.2, "Loading module 2"));
		    Thread.sleep(200);
		    notifyPreloader(new ProgressMessage(0.4, "Loading module 3"));
		    Thread.sleep(200);
		    notifyPreloader(new ProgressMessage(0.6, "Loading module 4"));
		    Thread.sleep(200);
		    notifyPreloader(new ProgressMessage(0.8, "Loading module 5"));
		    Thread.sleep(200);
		    notifyPreloader(new ProgressMessage(1.0, "Loading module 6"));
		    Thread.sleep(200);
	    }
	    catch(Exception e)
	    {
		    e.printStackTrace();
	    }
	    
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
		    workflowFilePaths = AppUtils.listResourceFiles(getClass().getPackage().getName(), ".bpmn20.xml");
	    }
	    catch(IOException | URISyntaxException e)
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
	    if(webserviceBaseUrl == null)
	    {
		    String errorCode = "E00-1008";
		    notifyPreloader(new ProgressMessage(null, errorCode));
		    return;
	    }
	
	    webserviceManager.init(webserviceBaseUrl);
	
	    Context.setManagers(configManager, workflowManager, webserviceManager);
	
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
	    LOGGER.traceExit();
    }
    
    @Override
    public void start(Stage ignoredStage)
    {
	    LOGGER.traceEntry();
	    
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
	    coreFxController.passInitialResources(errorsBundle, messagesBundle, appIcon);
	    coreFxController.getGuiState().setLanguage(initialLanguage);
	    
	    primaryStage.getScene().setNodeOrientation(initialLanguage.getNodeOrientation());
	    primaryStage.centerOnScreen();
	    primaryStage.setOnCloseRequest(event -> LOGGER.info("The main window is closed"));
	
	    notifyPreloader(ProgressMessage.SUCCESSFULLY_DONE);
	    
	    primaryStage.show();
	    LOGGER.info("The main window is shown");
	
	    // set minimum dimensions for the stage
	    primaryStage.setMinWidth(primaryStage.getWidth());
	    primaryStage.setMinHeight(primaryStage.getHeight());
	
	    LOGGER.traceExit();
    }
    
    public static void main(String[] args)
    {
	    LOGGER.traceEntry();
	    launch(args);
	    LOGGER.traceExit();
    }
}