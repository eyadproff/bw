package sa.gov.nic.bio.bw;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import retrofit2.Call;
import sa.gov.nic.bio.biokit.exceptions.JsonMappingException;
import sa.gov.nic.bio.biokit.utils.JsonMapper;
import sa.gov.nic.bio.biokit.websocket.UpdateListener;
import sa.gov.nic.bio.biokit.websocket.WebsocketLogger;
import sa.gov.nic.bio.biokit.websocket.beans.Message;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.MenuItem;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.biokit.BioKitManager;
import sa.gov.nic.bio.bw.core.controllers.CoreFxController;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.CombinedResourceBundle;
import sa.gov.nic.bio.bw.core.utils.ConfigManager;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.preloader.beans.PreloaderNotification;
import sa.gov.nic.bio.bw.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.core.tasks.LogoutTask;
import sa.gov.nic.bio.bw.core.webservice.LookupAPI;
import sa.gov.nic.bio.bw.core.beans.NicHijriCalendarData;
import sa.gov.nic.bio.bw.core.webservice.WebserviceManager;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.CoreWorkflow;
import sa.gov.nic.bio.bw.core.workflow.ResourceBundleProvider;
import sa.gov.nic.bio.bw.core.workflow.Workflow;
import sa.gov.nic.bio.bw.core.workflow.WorkflowManager;
import sa.gov.nic.bio.bw.utils.StartupErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

/**
 * The entry point of the application. Since Java 8, the main method is not alwaysRequired as long as the entry point
 * class extends <code>javafx.application.Application</code>. We kept the main method for old IDEs that don't
 * support this behavior.
 *
 * When the app preloader shows the splash screen, <code>AppEntryPoint.init()</code> starts. It is responsible for
 * the real initialization of the application. Whenever an error occurs, it notifies the app preloader with the error
 * details to show it on the dialog. When <code>init()</code> finishes, <code>AppEntryPoint.start()</code> starts on
 * the UI thread. <code>start()</code> is responsible for creating the primary stage. Once the primary stage is
 * ready, it notifies the app preloader with a success message in menuOrder to close the splash screen. After that,
 * the primary stage is shown.
 *
 * @author Fouad Almalki
 */
public class AppLauncher extends Application implements AppLogger
{
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
	private String windowTitle;
	private Pane rootPane;
	private Image appIcon;
	private CoreFxController coreFxController;
	private boolean successfulInit = false;
	
	@SuppressWarnings("unchecked")
    @Override
    public void init()
    {
	    LOGGER.entering(AppLauncher.class.getName(), "init()");
	
	    try
	    {
		    configManager.load();
	    }
	    catch(IOException e)
	    {
	    	String errorCode = StartupErrorCodes.C001_00005.getCode();
	    	String[] errorDetails = {"Failed to load the config file!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    String serverUrl = System.getProperty("bio.serverUrl");
	    String sRuntimeEnvironment = System.getProperty("jnlp.bio.runtime.environment", "LOCAL");
	    
	    RuntimeEnvironment runtimeEnvironment = RuntimeEnvironment.byName(sRuntimeEnvironment);
	    GuiLanguage guiLanguage = Context.getGuiLanguage();
	
	    try
	    {
		    stringsBundle = AppUtils.getCoreStringsResourceBundle(guiLanguage.getLocale());
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
		    appIcon = new Image(CommonImages.ICON_APP.getAsInputStream());
	    }
	    catch(Exception e)
	    {
		    String errorCode = StartupErrorCodes.C001_00007.getCode();
		    String[] errorDetails = {"Failed to load the app icon!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    URL fxmlUrl = AppUtils.getCoreFxmlFileAsResource();
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
		    String[] userChoice = new String[1];
		
		    Platform.runLater(() ->
		    {
			    String dialogTitle = stringsBundle.getString("dialog.choice.title");
			    String headerText = stringsBundle.getString("dialog.choice.selectServer.headerText");
			    String buttonText = stringsBundle.getString("dialog.choice.selectServer.button");
			
			    userChoice[0] = DialogUtils.showChoiceDialog(null, null, dialogTitle,
		                                         headerText, urls, buttonText, true,
                                                guiLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT);
			    latch.countDown();
		    });
		
		    try
		    {
			    latch.await(); // wait until the user choose a url
			    serverUrl = userChoice[0];
			    
			    if(serverUrl == null)
			    {
			    	Platform.exit();
			    	return;
			    }
		    }
		    catch(InterruptedException e)
		    {
			    e.printStackTrace();
		    }
		
	    }
	
	    if(serverUrl == null)
	    {
		    String errorCode = StartupErrorCodes.C001_00009.getCode();
		    String[] errorDetails = {"\"serverUrl\" is null!"};
		    notifyPreloader(PreloaderNotification.failure(null, errorCode, errorDetails));
		    return;
	    }
	
	    LOGGER.info("serverUrl = " + serverUrl);
	
	    // default values
	    int readTimeoutSeconds = 60; // 1 minute
	    int connectTimeoutSeconds = 60; // 1 minute
	
	    String sReadTimeoutSeconds = configManager.getProperty("webservice.readTimeoutSeconds");
	    String sConnectTimeoutSeconds = configManager.getProperty("webservice.connectTimeoutSeconds");
	    
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
	
	    try
	    {
		    webserviceManager.init(serverUrl, readTimeoutSeconds, connectTimeoutSeconds);
	    }
	    catch(Exception e)
	    {
		    String errorCode = StartupErrorCodes.C001_00010.getCode();
		    String[] errorDetails = {"Failed to initialize the webservice manager!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    String appVersion = configManager.getProperty("app.version");
	
	    if(appVersion == null)
	    {
		    String errorCode = StartupErrorCodes.C001_00011.getCode();
		    String[] errorDetails = {"Config \"app.appVersion\" is missing!"};
		    notifyPreloader(PreloaderNotification.failure(null, errorCode, errorDetails));
		    return;
	    }
	    
	    Context.attachAppVersion(appVersion);
	
	    LOGGER.info("appVersion = " + appVersion);
	    LOGGER.info("runtime-environment = " + runtimeEnvironment);
	
	    LookupAPI lookupAPI = webserviceManager.getApi(LookupAPI.class);
	    Call<Map<String, String>> apiCall = lookupAPI.lookupAppConfigs(AppConstants.APP_CODE);
	    TaskResponse<Map<String, String>> webTaskResponse = webserviceManager.executeApi(apiCall);
	
	    if(webTaskResponse.isSuccess())
	    {
		    Map<String, String> appConfigs = webTaskResponse.getResult();
		    configManager.addProperties(appConfigs);
	    }
	    else
	    {
		    String errorCode = webTaskResponse.getErrorCode();
		    String[] errorDetails = webTaskResponse.getErrorDetails();
		    Exception exception = webTaskResponse.getException();
		    notifyPreloader(PreloaderNotification.failure(exception, errorCode, errorDetails));
		    return;
	    }
	
	    
	
	    String sResponseTimeoutSeconds = configManager.getProperty("biokit.responseTimeoutSeconds");
	
	    String sMaxTextMessageBufferSizeInBytes =
			    configManager.getProperty("biokit.maxTextMessageBufferSizeInBytes");
	    String sMaxBinaryMessageBufferSizeInBytes =
			    configManager.getProperty("biokit.maxBinaryMessageBufferSizeInBytes");
	    
	    // default values
	    int maxTextMessageBufferSizeInBytes = 60; // 10 MB
	    int maxBinaryMessageBufferSizeInBytes = 60; // 10 MB
	    int responseTimeoutSeconds = 30; // 30 seconds
	    int biokitWebsocketPort;
	
	    if(sMaxTextMessageBufferSizeInBytes == null)
	    {
		    LOGGER.warning("sMaxTextMessageBufferSizeInBytes is null! Default value is "
				                                                                    + maxTextMessageBufferSizeInBytes);
	    }
	    else try
	    {
		    maxTextMessageBufferSizeInBytes = Integer.parseInt(sMaxTextMessageBufferSizeInBytes);
		    LOGGER.info("maxTextMessageBufferSizeInBytes = " + maxTextMessageBufferSizeInBytes);
	    }
	    catch(NumberFormatException e)
	    {
		    LOGGER.warning("Failed to parse sMaxTextMessageBufferSizeInBytes as int! " +
				                   "sMaxTextMessageBufferSizeInBytes = " + sMaxTextMessageBufferSizeInBytes);
	    }
	
	    if(sMaxBinaryMessageBufferSizeInBytes == null)
	    {
		    LOGGER.warning("sMaxBinaryMessageBufferSizeInBytes is null! Default value is "
				                                                                + maxBinaryMessageBufferSizeInBytes);
	    }
	    else try
	    {
		    maxBinaryMessageBufferSizeInBytes = Integer.parseInt(sMaxBinaryMessageBufferSizeInBytes);
		    LOGGER.info("maxBinaryMessageBufferSizeInBytes = " + maxBinaryMessageBufferSizeInBytes);
	    }
	    catch(NumberFormatException e)
	    {
		    LOGGER.warning("Failed to parse sMaxBinaryMessageBufferSizeInBytes as int! " +
				                   "sMaxBinaryMessageBufferSizeInBytes = " + sMaxBinaryMessageBufferSizeInBytes);
	    }
	
	    if(sResponseTimeoutSeconds == null)
	    {
		    LOGGER.warning("sResponseTimeoutSeconds is null! Default value is " + responseTimeoutSeconds);
	    }
	    else try
	    {
		    responseTimeoutSeconds = Integer.parseInt(sResponseTimeoutSeconds);
		    LOGGER.info("responseTimeoutSeconds = " + responseTimeoutSeconds);
	    }
	    catch(NumberFormatException e)
	    {
		    LOGGER.warning("Failed to parse sResponseTimeoutSeconds as int! sResponseTimeoutSeconds = "
				                                                                            + sResponseTimeoutSeconds);
	    }
	
	    String biokitWebsocketUrl = configManager.getProperty("biokit.serverUrl");
	    String sBiokitWebsocketPort = configManager.getProperty("biokit.port");
	    String biokitBclId = configManager.getProperty("biokit.bclId");
	
	    if(biokitWebsocketUrl == null)
	    {
		    String errorCode = StartupErrorCodes.C001_00012.getCode();
		    String[] errorDetails = {"\"biokitWebsocketUrl\" is null!"};
		    notifyPreloader(PreloaderNotification.failure(null, errorCode, errorDetails));
		    return;
	    }
	
	    if(sBiokitWebsocketPort == null)
	    {
		    String errorCode = StartupErrorCodes.C001_00013.getCode();
		    String[] errorDetails = {"\"sBiokitWebsocketPort\" is null!"};
		    notifyPreloader(PreloaderNotification.failure(null, errorCode, errorDetails));
		    return;
	    }
	    else try
	    {
		    biokitWebsocketPort = Integer.parseInt(sBiokitWebsocketPort);
		    LOGGER.info("biokitWebsocketPort = " + biokitWebsocketPort);
	    }
	    catch(NumberFormatException e)
	    {
		    String errorCode = StartupErrorCodes.C001_00014.getCode();
		    String[] errorDetails = {"\"sBiokitWebsocketPort\" is not int!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    if(biokitBclId == null)
	    {
		    String errorCode = StartupErrorCodes.C001_00015.getCode();
		    String[] errorDetails = {"\"biokitBclId\" is null!"};
		    notifyPreloader(PreloaderNotification.failure(null, errorCode, errorDetails));
		    return;
	    }
	
	    JsonMapper<Message> jsonMapper = new JsonMapper<>()
	    {
		    @Override
		    public Message fromJson(String json) throws JsonMappingException
		    {
			    try
			    {
				    return AppUtils.fromJson(json, Message.class);
			    }
			    catch(Exception e)
			    {
				    throw new JsonMappingException(e);
			    }
		    }
		
		    @Override
		    public String toJson(Message object) throws JsonMappingException
		    {
			    try
			    {
				    return AppUtils.toJson(object);
			    }
			    catch(Exception e)
			    {
				    throw new JsonMappingException(e);
			    }
		    }
	    };
	
	    WebsocketLogger biokitWebsocketLogger = new WebsocketLogger()
	    {
		    @Override
		    public void logConnectionOpening()
		    {
			    LOGGER.info("A new connection to BioKit is established.");
		    }
		
		    @Override
		    public void logConnectionClosure(String closeCode, String reasonPhrase)
		    {
		    	String sCloseReason = "closeCode = " + closeCode + " - reasonPhrase = " + reasonPhrase;
			    LOGGER.info("The connection to BioKit is closed, closeReason = " + sCloseReason);
		    }
		
		    @Override
		    public void logError(Throwable throwable)
		    {
			    LOGGER.log(Level.SEVERE, "An error occurs with connection to BioKit!", throwable);
		    }
		
		    @Override
		    public void logNewMessage(Message message)
		    {
			    LOGGER.fine(message.toShortString());
		    }
	    };
	
	    UpdateListener biokitUpdateListener = () -> LOGGER.info("New update for BioKit is available!");
	
	    BioKitManager bioKitManager = new BioKitManager(biokitBclId, biokitWebsocketPort, biokitWebsocketUrl,
                            maxTextMessageBufferSizeInBytes, maxBinaryMessageBufferSizeInBytes, responseTimeoutSeconds,
                            jsonMapper, null, biokitWebsocketLogger, biokitUpdateListener);
	
	    List<MenuItem> subMenus = new ArrayList<>();
	    Map<String, MenuItem> topMenus = new HashMap<>();
	
	    List<Class<?>> appClasses;
	    try
	    {
		    appClasses = AppUtils.listClasses(getClass().getProtectionDomain(), runtimeEnvironment);
	    }
	    catch(Exception e)
	    {
		    String errorCode = StartupErrorCodes.C001_00019.getCode();
		    String[] errorDetails = {"Failed to load the app classes!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	    
	    Map<Class<? extends Workflow>, AssociatedMenu> workflowMenuClasses = new HashMap<>();
	    Map<String, ResourceBundleProvider> moduleResourceBundleProviders = new HashMap<>();
	
	    for(Class appClass : appClasses)
	    {
		    AssociatedMenu associatedMenu = (AssociatedMenu) appClass.getAnnotation(AssociatedMenu.class);
		    if(associatedMenu != null) workflowMenuClasses.put(appClass, associatedMenu);
		    
		    if(ResourceBundleProvider.class.isAssignableFrom(appClass) && !Modifier.isAbstract(appClass.getModifiers())
				                                                       && appClass != ResourceBundleProvider.class)
		    {
			    try
			    {
				    ResourceBundleProvider resourceBundleProvider = (ResourceBundleProvider)
                                                    AppUtils.instantiateClassByReflection(appClass.getConstructor());
				    moduleResourceBundleProviders.put(appClass.getModule().getName(), resourceBundleProvider);
			    }
			    catch(Exception e)
			    {
				    e.printStackTrace();
			    }
		    }
	    }
	    
	    // add resource bundle of bw.core module as well
	    moduleResourceBundleProviders.put(CoreWorkflow.class.getModule().getName(), new ResourceBundleProvider()
	    {
		    @Override
		    public ResourceBundle getStringsResourceBundle(Locale locale)
		    {
			    return AppUtils.getCoreStringsResourceBundle(locale);
		    }
		
		    @Override
		    public ResourceBundle getErrorsResourceBundle(Locale locale)
		    {
			    return AppUtils.getCoreErrorsResourceBundle(locale);
		    }
	    });
	
	    CombinedResourceBundle errorsBundle = new CombinedResourceBundle(moduleResourceBundleProviders.values(),
	                                                                     guiLanguage.getLocale());
	    errorsBundle.load();
	
	    for(Entry<Class<? extends Workflow>, AssociatedMenu> workflowMenuClass : workflowMenuClasses.entrySet())
	    {
		    Class<? extends Workflow> workflowClass = workflowMenuClass.getKey();
		    AssociatedMenu associatedMenu = workflowMenuClass.getValue();
		
		    MenuItem menuItem = new MenuItem();
		    menuItem.setMenuId(associatedMenu.menuId());
		    menuItem.setLabel(associatedMenu.menuTitle());
		    menuItem.setOrder(associatedMenu.menuOrder());
		    menuItem.setDevices(new HashSet<>(Arrays.asList(associatedMenu.devices())));
		    menuItem.setWorkflowClass(workflowClass);
		
		    String topMenu = associatedMenu.menuId().substring(0, associatedMenu.menuId().lastIndexOf('.'));
		    if(!topMenus.containsKey(topMenu))
		    {
			    String icon = configManager.getProperty(topMenu + ".icon");
			    int order = Integer.parseInt(configManager.getProperty(topMenu + ".order"));
			
			    MenuItem topMenuItem = new MenuItem();
			    topMenuItem.setMenuId(topMenu);
			    topMenuItem.setLabel(topMenu);
			    topMenuItem.setIconId(icon);
			    topMenuItem.setOrder(order);
			
			    topMenus.put(topMenu, topMenuItem);
		    }
		    
		    subMenus.add(menuItem);
	    }
	
	    Context.attach(runtimeEnvironment, configManager, workflowManager, webserviceManager, bioKitManager,
	                   executorService, scheduledExecutorService, errorsBundle, new UserSession(), serverUrl, topMenus,
	                   subMenus, moduleResourceBundleProviders);
	
	    Call<NicHijriCalendarData> apiCall2 = lookupAPI.lookupNicHijriCalendarData();
	    TaskResponse<NicHijriCalendarData> webTaskResponse2 = webserviceManager.executeApi(apiCall2);
	
	    if(webTaskResponse2.isSuccess())
	    {
		    NicHijriCalendarData nicHijriCalendarData = webTaskResponse2.getResult();
		    try
		    {
			    AppUtils.injectNicHijriCalendarData(nicHijriCalendarData);
		    }
		    catch(Exception e)
		    {
			    String errorCode = StartupErrorCodes.C001_00016.getCode();
			    String[] errorDetails = {"Failed to inject NicHijriCalendarData!"};
			    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
			    return;
		    }
	    }
	    else
	    {
		    Exception exception = webTaskResponse2.getException();
		    String errorCode = webTaskResponse2.getErrorCode();
		    String[] errorDetails = webTaskResponse2.getErrorDetails();
		    notifyPreloader(PreloaderNotification.failure(exception, errorCode, errorDetails));
		    return;
	    }
	
	    String title;
	    try
	    {
		    title = stringsBundle.getString("window.title") + " " + appVersion + " (" +
				    stringsBundle.getString("label.environment." +
						                            Context.getRuntimeEnvironment().name().toLowerCase()) + ")";
	    }
	    catch(MissingResourceException e)
	    {
		    String errorCode = StartupErrorCodes.C001_00017.getCode();
		    String[] errorDetails = {"Label text \"window.menuTitle\" is missing!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    windowTitle = AppUtils.localizeNumbers(title, Locale.getDefault(), false);
	
	    FXMLLoader rootPaneLoader = new FXMLLoader(fxmlUrl, stringsBundle);
	
	    try
	    {
		    rootPane = rootPaneLoader.load();
	    }
	    catch(IOException e)
	    {
		    String errorCode = StartupErrorCodes.C001_00018.getCode();
		    String[] errorDetails = {"Failed to load the core FXML correctly!"};
		    notifyPreloader(PreloaderNotification.failure(e, errorCode, errorDetails));
		    return;
	    }
	
	    coreFxController = rootPaneLoader.getController();
	    
	    successfulInit = true;
	    LOGGER.exiting(AppLauncher.class.getName(), "init()");
    }
    
    @Override
    public void start(Stage primaryStage)
    {
	    LOGGER.entering(AppLauncher.class.getName(), "start(Stage primaryStage)");
	    
	    if(!successfulInit) return;
	
	    Scene scene = new Scene(rootPane);
	    scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
	    {
		    if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
		    {
		    	AppUtils.showScenicView(scene);
			    event.consume();
		    }
		    else if(AppConstants.SHOWING_MOCK_TASKS_KEY_COMBINATION.match(event))
		    {
			    coreFxController.showMockTasksCheckBox();
			    event.consume();
		    }
	    });
	
	    primaryStage.getIcons().setAll(appIcon);
	    primaryStage.setTitle(windowTitle);
	    primaryStage.setWidth(AppConstants.STAGE_WIDTH);
	    primaryStage.setHeight(AppConstants.STAGE_HEIGHT);
	    primaryStage.setScene(scene);
	    primaryStage.getScene().setNodeOrientation(Context.getGuiLanguage().getNodeOrientation());
	    primaryStage.centerOnScreen();
	    primaryStage.setOnCloseRequest(GuiUtils.createOnExitHandler(primaryStage, coreFxController, new LogoutTask()));
	
	    coreFxController.registerStage(primaryStage);
	    notifyPreloader(PreloaderNotification.success());
	    primaryStage.show();
	    LOGGER.info("The main window is shown");
	
	    // set minimum dimensions for the stage
	    primaryStage.setMinWidth(primaryStage.getWidth());
	    primaryStage.setMinHeight(primaryStage.getHeight());
	
	    LOGGER.exiting(AppLauncher.class.getName(), "start(Stage ignoredStage)");
    }
    
    public static void main(String[] args) // optional
    {
	    LOGGER.entering(AppLauncher.class.getName(), "main(String[] args)");
	    launch(args);
	    LOGGER.exiting(AppLauncher.class.getName(), "main(String[] args)");
    }
}