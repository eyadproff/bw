package sa.gov.nic.bio.bw.client.core;

import com.sun.javafx.stage.StageHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.interfaces.ControllerResourcesLocator;
import sa.gov.nic.bio.bw.client.core.interfaces.IdleMonitorRegisterer;
import sa.gov.nic.bio.bw.client.core.interfaces.PersistableEntity;
import sa.gov.nic.bio.bw.client.core.interfaces.ResourceBundleCollection;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.CombinedResourceBundle;
import sa.gov.nic.bio.bw.client.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.IdleMonitor;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.wizard.WizardPane;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.SignalType;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.home.workflow.HomeWorkflow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * The JavaFX controller of the primary stage, i.e. controller of the whole application.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public class CoreFxController implements IdleMonitorRegisterer, PersistableEntity
{
	private static final Logger LOGGER = Logger.getLogger(CoreFxController.class.getName());
	public static final String FXML_FILE = "sa/gov/nic/bio/bw/client/core/fxml/core.fxml";
	public static final String RB_LABELS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/strings";
	public static final String RB_TOP_MENUS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/top_menus";
	
	// the following are here only to avoid warnings in FXML files.
	@FXML private Pane headerPane;
	@FXML private Pane menuPane;
	@FXML private Pane footerPane;
	
	@FXML private Stage primaryStage;
	@FXML private StackPane overlayPane;
	@FXML private HeaderPaneFxController headerPaneController;
	@FXML private FooterPaneFxController footerPaneController;
	@FXML private MenuPaneFxController menuPaneController;
	@FXML private NotificationPane idleNotifier;
	@FXML private NotificationPane notificationPane;
	@FXML private BorderPane bodyPane;
	@FXML private WizardPane wizardPane;
	
	private ResourceBundle stringsBundle;
	private ResourceBundle topMenusBundle;
	private String windowTitle;
	
	private IdleMonitor idleMonitor;
	private GuiLanguage currentLanguage;
	private BodyFxControllerBase currentBodyController;
	
	public void passInitialResources(ResourceBundle stringsBundle, ResourceBundle topMenusBundle, String windowTitle,
	                                 GuiLanguage language)
	{
		this.stringsBundle = stringsBundle;
		this.topMenusBundle = topMenusBundle;
		this.windowTitle = windowTitle;
		this.currentLanguage = language;
	}
	
	public Stage getPrimaryStage(){return primaryStage;}
	public NotificationPane getNotificationPane(){return notificationPane;}
	public BorderPane getBodyPane(){return bodyPane;}
	public GuiLanguage getCurrentLanguage(){return currentLanguage;}
	
	public HeaderPaneFxController getHeaderPaneController(){return headerPaneController;}
	public FooterPaneFxController getFooterPaneController(){return footerPaneController;}
	public MenuPaneFxController getMenuPaneController(){return menuPaneController;}
	public ResourceBundle getStringsBundle(){return stringsBundle;}
	public ResourceBundle getTopMenusBundle(){return topMenusBundle;}
	
	@FXML
	private void initialize()
	{
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
		{
			String errorCode = CoreErrorCodes.C002_00016.getCode();
			String[] errorDetails = {"Uncaught exception!"};
			showErrorDialog(errorCode, throwable, errorDetails);
		});
		
		idleMonitor = new IdleMonitor(this::onShowingIdleWarning, this::onIdle, this::onIdleInterrupt,
		                              this::onTick, idleNotifier);
		
		if(!Context.getWorkflowManager().isRunning())
		{
			Context.getWorkflowManager().startCoreWorkflow(this::renderBodyForm);
		}
	}
	
	/**
	 * Called once for every primary stage, when it is shown. It is called in the UI-thread.
	 * It is called after initialize() and passInitialResources().
	 */
	@FXML
	private void onStageShown(WindowEvent windowEvent)
	{
		primaryStage.setTitle(windowTitle);
		
		// attach this controller to the sub-controllers.
		headerPaneController.attachCoreFxController(this);
		footerPaneController.attachCoreFxController(this);
		menuPaneController.attachCoreFxController(this);
		
		// fix the size of the header pane and the menu pane.
		headerPaneController.getRegionRootPane().setMinSize(headerPaneController.getRegionRootPane().getWidth(),
		                                                    headerPaneController.getRegionRootPane().getHeight());
		menuPaneController.getRegionRootPane().setMinSize(menuPaneController.getRegionRootPane().getWidth(),
		                                                  menuPaneController.getRegionRootPane().getHeight());
	}
	
	/**
	 * Called when the user is submitted the form. It sends the form data to the workflow manager.
	 *
	 * @param uiDataMap the data submitted by the user when filling the form
	 */
	public void submitForm(Map<String, Object> uiDataMap)
	{
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	public void logout()
	{
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(Workflow.KEY_SIGNAL_TYPE, SignalType.LOGOUT);
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	public void goToMenu(Class<?> menuWorkflowClass)
	{
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(Workflow.KEY_SIGNAL_TYPE, SignalType.MENU_NAVIGATION);
		dataMap.put(HomeWorkflow.KEY_MENU_WORKFLOW_CLASS, menuWorkflowClass);
		Context.getWorkflowManager().submitUserTask(dataMap);
	}
	
	/**
	 * A callback that is called by the workflow manager to render the body form. This method is called from
	 * a non-UI thread.
	 *
	 * @param controllerClass class of the new/existing controller class of the body
	 * @param uiInputData data passed by the workflow manager to the controller
	 */
	private void renderBodyForm(Class<?> controllerClass, Map<String, Object> uiInputData)
	{
		Platform.runLater(() ->
		{
			if(currentBodyController != null && currentBodyController.getClass() == controllerClass) // same form
			{
				currentBodyController.onWorkflowUserTaskLoad(false, uiInputData);
			}
			else // new form
			{
				try
				{
					ControllerResourcesLocator controllerResourcesLocator = (ControllerResourcesLocator)
																controllerClass.getDeclaredConstructor().newInstance();
					currentBodyController = renderNewBodyForm(controllerResourcesLocator, uiInputData);
					if(currentBodyController != null)
					{
						currentBodyController.onWorkflowUserTaskLoad(true, uiInputData);
					}
				}
				catch(InstantiationException | IllegalAccessException | NoSuchMethodException |
					  InvocationTargetException e)
				{
					String errorCode = CoreErrorCodes.C002_00001.getCode();
					String[] errorDetails = {"Failed to load the class " + controllerClass.getName()};
					showErrorDialog(errorCode, e, errorDetails);
				}
			}
		});
	}
	
	/**
	 * Render the body form by first locating its FXML file and its resource bundles and load them. Next, replace
	 * the existing body node with the created node.
	 *
	 * @param controllerResourcesLocator a locator for the FXML file and the resource bundles
	 * @param uiInputData data passed by the workflow manager to the controller
	 *
	 * @return the created JavaFX controller
	 */
	private BodyFxControllerBase renderNewBodyForm(ControllerResourcesLocator controllerResourcesLocator,
	                                               Map<String, Object> uiInputData)
	{
		notificationPane.hide();
		
		URL fxmlUrl = controllerResourcesLocator.getFxmlLocation();
		if(fxmlUrl == null)
		{
			String errorCode = CoreErrorCodes.C002_00002.getCode();
			String[] errorDetails = {"\"fxmlUrl\" is null!", "controllerClass = " +
									controllerResourcesLocator.getClass().getName()};
			showErrorDialog(errorCode, null, errorDetails);
			return null;
		}
		
		ResourceBundleCollection resourceBundleCollection = controllerResourcesLocator.getResourceBundleCollection();
		
		ResourceBundle stringsBundle;
		try
		{
			stringsBundle = ResourceBundle.getBundle(resourceBundleCollection.getStringsBundlePath(),
			                                        currentLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = CoreErrorCodes.C002_00003.getCode();
			String[] errorDetails = {"\"stringsBundle\" resource bundle is missing!", "controllerClass = " +
									controllerResourcesLocator.getClass().getName()};
			showErrorDialog(errorCode, e, errorDetails);
			return null;
		}
		
		FXMLLoader paneLoader = new FXMLLoader(fxmlUrl, stringsBundle);
		
		Node loadedPane;
		try
		{
			loadedPane = paneLoader.load();
		}
		catch(IOException e)
		{
			String errorCode = CoreErrorCodes.C002_00004.getCode();
			String[] errorDetails = {"Failed to load FXML correctly!", "controllerClass = " +
									controllerResourcesLocator.getClass().getName()};
			showErrorDialog(errorCode, e, errorDetails);
			return null;
		}
		
		BodyFxControllerBase bodyFxController = paneLoader.getController();
		bodyFxController.attachCoreFxController(this);
		bodyFxController.attachResourceBundles(stringsBundle);
		
		bodyPane.setCenter(loadedPane);
		
		if(bodyFxController instanceof WizardStepFxControllerBase)
		{
			if(wizardPane == null)
			{
				URL wizardFxmlLocation = ((WizardStepFxControllerBase) bodyFxController).getWizardFxmlLocation();
				FXMLLoader wizardPaneLoader = new FXMLLoader(wizardFxmlLocation, stringsBundle);
				
				try
				{
					wizardPane = wizardPaneLoader.load();
				}
				catch(IOException e)
				{
					String errorCode = CoreErrorCodes.C002_00005.getCode();
					String[] errorDetails = {"Failed to load FXML correctly!", "controllerClass = " +
											bodyFxController.getClass().getName()};
					showErrorDialog(errorCode, e, errorDetails);
					return null;
				}
				
				wizardPane.goNext();
			}
			else // change the workflow indicator
			{
				String direction = (String) uiInputData.get("direction");
				if("backward".equals(direction)) wizardPane.goPrevious();
				else if("forward".equals(direction)) wizardPane.goNext();
				else if("startOver".equals(direction)) wizardPane.startOver();
			}
		}
		else wizardPane = null;
		
		bodyPane.setTop(wizardPane);
		bodyFxController.onControllerReady();
		
		return bodyFxController;
	}
	
	// must be called from UI thread
	public boolean showConfirmationDialogAndWait(String headerText, String contentMessage)
	{
		String title = stringsBundle.getString("dialog.confirm.title");
		String buttonConfirmText = stringsBundle.getString("dialog.confirm.buttons.confirm");
		String buttonCancelText = stringsBundle.getString("dialog.confirm.buttons.cancel");
		boolean rtl = currentLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		return DialogUtils.showConfirmationDialog(primaryStage, this, title, headerText,
		                                          contentMessage, buttonConfirmText, buttonCancelText, rtl);
	}
	
	public void showErrorDialog(String errorCode, Throwable throwable, String[] errorDetails)
	{
		Platform.runLater(() ->
		{
			String title = stringsBundle.getString("dialog.error.title");
			String headerText = stringsBundle.getString("dialog.error.header");
			String buttonOkText = stringsBundle.getString("dialog.error.buttons.ok");
			String buttonMoreDetailsText = stringsBundle.getString("dialog.error.buttons.showErrorDetails");
			String buttonLessDetailsText = stringsBundle.getString("dialog.error.buttons.hideErrorDetails");
			boolean rtl = currentLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			
			String contentMessage = String.format(stringsBundle.getString("message.errorOccurs"), errorCode);
			
			StringBuilder sb = new StringBuilder();
			GuiUtils.buildErrorMessage(throwable, errorDetails, sb);
			
			LOGGER.severe(contentMessage + (sb.length() > 0 ? "\n" : "") + sb.toString());
			DialogUtils.showAlertDialog(AlertType.ERROR, primaryStage, this, title, headerText,
			                            contentMessage, sb.toString(), buttonOkText, buttonMoreDetailsText,
			                            buttonLessDetailsText, rtl);
		});
	}
	
	/*
	***************************************************************************************************************
							THE FOLLOWING SECTION IS FOR SWITCHING THE GUI LANGUAGE
	***************************************************************************************************************
	*/
	
	/**
	 * Switch the language of the application to a different language.
	 *
	 * @param toLanguage the language to apply to the application GUI
	 * @param persistableEntity the entity to persist its state
	 */
	public void switchLanguage(GuiLanguage toLanguage, PersistableEntity persistableEntity)
	{
		if(currentLanguage == toLanguage)
		{
			LOGGER.warning("cannot switch the language to the current language: " + currentLanguage.name());
			return;
		}
		
		LOGGER.info("Switching the GUI language to " + toLanguage.name());
		
		List<String> errorBundleNames;
		
		try
		{
			errorBundleNames = AppUtils.listResourceFiles(getClass().getProtectionDomain(),
			                                              ".*/errors.properties$",
			                                              true, Context.getRuntimeEnvironment());
		}
		catch(Exception e)
		{
			String errorCode = CoreErrorCodes.C002_00006.getCode();
			String[] errorDetails = {"Failed to load the error bundles!"};
			showErrorDialog(errorCode, e, errorDetails);
			return;
		}
		
		ResourceBundle errorsBundle = new CombinedResourceBundle(errorBundleNames, toLanguage.getLocale(),
		                                                         new UTF8Control());
		((CombinedResourceBundle) errorsBundle).load();
		Context.setErrorsBundle(errorsBundle);
		
		ResourceBundle stringsBundle;
		try
		{
			stringsBundle = ResourceBundle.getBundle(RB_LABELS_FILE, toLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = CoreErrorCodes.C002_00007.getCode();
			String[] errorDetails = {"Core \"stringsBundle\" resource bundle is missing!"};
			showErrorDialog(errorCode, e, errorDetails);
			return;
		}
		
		ResourceBundle topMenusBundle;
		try
		{
			topMenusBundle = ResourceBundle.getBundle(RB_TOP_MENUS_FILE, toLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = CoreErrorCodes.C002_00008.getCode();
			String[] errorDetails = {"Core \"topMenusBundle\" resource bundle is missing!"};
			showErrorDialog(errorCode, e, errorDetails);
			return;
		}
		
		URL fxmlUrl = Thread.currentThread().getContextClassLoader().getResource(FXML_FILE);
		if(fxmlUrl == null)
		{
			String errorCode = CoreErrorCodes.C002_00009.getCode();
			String[] errorDetails = {"Core \"fxmlUrl\" is null!"};
			showErrorDialog(errorCode, null, errorDetails);
			return;
		}
		
		String version = Context.getConfigManager().getProperty("app.version");
		String title = stringsBundle.getString("window.title") + " " + version + " (" +
					   stringsBundle.getString("label.environment." +
							                  Context.getRuntimeEnvironment().name().toLowerCase()) + ")";
		windowTitle = AppUtils.replaceNumbersOnly(title, Locale.getDefault());
		
		FXMLLoader newStageLoader = new FXMLLoader(fxmlUrl, stringsBundle);
		
		Stage oldStage = primaryStage;
		
		Stage newStage;
		try
		{
			newStage = newStageLoader.load();
		}
		catch(IOException e)
		{
			String errorCode = CoreErrorCodes.C002_00010.getCode();
			String[] errorDetails = {"Failed to load core FXML correctly!"};
			showErrorDialog(errorCode, e, errorDetails);
			return;
		}
		
		newStage.getScene().setNodeOrientation(toLanguage.getNodeOrientation());
		
		StateBundle oldState = new StateBundle();
		persistableEntity.onSaveState(oldState);
		this.onSaveState(oldState);
		
		CoreFxController newCoreFxController = newStageLoader.getController();
		newStage.setOnCloseRequest(GuiUtils.createOnExitHandler(primaryStage, newCoreFxController));
		
		newCoreFxController.currentBodyController = currentBodyController;
		newCoreFxController.passInitialResources(stringsBundle, topMenusBundle, windowTitle, toLanguage);
		
		Context.getWorkflowManager().changeFormRenderer(newCoreFxController::renderBodyForm);
		
		// save the language for later usage
		Preferences prefs = Preferences.userNodeForPackage(AppConstants.PREF_NODE_CLASS);
		prefs.put(AppConstants.UI_LANGUAGE_PREF_NAME, toLanguage.getLocale().getLanguage());
		
		boolean success = newCoreFxController.applyStateBundle(oldState);
		
		if(!success) return;
		
		oldStage.hide();
		LOGGER.info("The old main window is closed");
		newStage.show();
		LOGGER.info("The new main window is shown");
		
		// set minimum dimensions for the stage
		newStage.setMinWidth(oldStage.getMinWidth());
		newStage.setMinHeight(oldStage.getMinHeight());
	}
	
	/**
	 * Applying the state bundle to the current stage and the current body JavaFX controller.
	 *
	 * @param stateBundle the state bundle to be applied
	 *
	 * @return <code>true</code> if succeeded, otherwise <code>false</code>
	 */
	private boolean applyStateBundle(StateBundle stateBundle)
	{
		BodyFxControllerBase newBodyController = renderNewBodyForm(currentBodyController, new HashMap<>());
		currentBodyController = newBodyController;
		
		if(newBodyController instanceof PersistableEntity)
		{
			((PersistableEntity) newBodyController).onLoadState(stateBundle);
			this.onLoadState(stateBundle);
			return true;
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			sb.append("newBodyController is not an instance of PersistableEntity! ");
			
			if(newBodyController == null) sb.append("newBodyController = null");
			else sb.append("newBodyController type = ").append(newBodyController.getClass().getName());
			
			LOGGER.warning(sb.toString());
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSaveState(StateBundle stateBundle)
	{
		stateBundle.putData("stageWidth", primaryStage.getWidth());
		stateBundle.putData("stageHeight", primaryStage.getHeight());
		stateBundle.putData("stageX", primaryStage.getX());
		stateBundle.putData("stageY", primaryStage.getY());
		stateBundle.putData("stageMaximized", primaryStage.isMaximized());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLoadState(StateBundle stateBundle)
	{
		double width = stateBundle.getDate("stageWidth", Double.class);
		double height = stateBundle.getDate("stageHeight", Double.class);
		double x = stateBundle.getDate("stageX", Double.class);
		double y = stateBundle.getDate("stageY", Double.class);
		boolean maximized = stateBundle.getDate("stageMaximized", Boolean.class);
		
		if(maximized) primaryStage.setMaximized(true);
		else
		{
			primaryStage.setWidth(width);
			primaryStage.setHeight(height);
			primaryStage.setX(x);
			primaryStage.setY(y);
		}
	}
	
	/*
	***************************************************************************************************************
								THE FOLLOWING SECTION IS RELATED TO THE IDLE MONITOR
	***************************************************************************************************************
	*/
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void registerStageForIdleMonitoring(Stage stage)
	{
		idleMonitor.register(stage, Event.ANY);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregisterStageForIdleMonitoring(Stage stage)
	{
		idleMonitor.unregister(stage, Event.ANY);
	}
	
	/**
	 * Start the idle monitoring.
	 */
	public void startIdleMonitor()
	{
		registerStageForIdleMonitoring(primaryStage);
		idleMonitor.startMonitoring();
	}
	
	/**
	 * Stop the idle monitoring.
	 */
	public void stopIdleMonitor()
	{
		unregisterStageForIdleMonitoring(primaryStage);
		idleMonitor.stopMonitoring();
	}
	
	/**
	 * Callback that is called the second period of the idle monitor starts.
	 *
	 * @param remainingSeconds the remaining seconds until the session timeout
	 */
	private void onShowingIdleWarning(int remainingSeconds)
	{
		idleNotifier.show();
		setIdleWarningRemainingSeconds(remainingSeconds);
	}
	
	/**
	 * Callback that is called every second when the second period of the idle monitor is active.
	 *
	 * @param remainingSeconds the remaining seconds until the session timeout
	 */
	private void onTick(Integer remainingSeconds)
	{
		setIdleWarningRemainingSeconds(remainingSeconds);
	}
	
	/**
	 * Callback that is called when the session is timed out because of the user being inactive
	 */
	private void onIdle()
	{
		idleNotifier.hide();
		stopIdleMonitor();
		Context.getWebserviceManager().cancelRefreshTokenScheduler();
		
		Platform.runLater(() ->
		{
		    String title = stringsBundle.getString("dialog.idle.title");
		    String contentText = stringsBundle.getString("dialog.idle.content");
		    String buttonText = stringsBundle.getString("dialog.idle.buttons.goToLogin");
		    boolean rtl = currentLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		    overlayPane.setVisible(true);
			
			// make sure the stage is not iconified, otherwise the dialog will be invisible
			primaryStage.setIconified(false);
		    DialogUtils.showWarningDialog(primaryStage, this, title, null,
		                                  contentText, buttonText, rtl);
		    overlayPane.setVisible(false);
			
			// close all opened dialogs (except the primary one)
			ObservableList<Stage> stages = StageHelper.getStages();
			for(int i = 1; i <= stages.size(); i++)
			{
				Stage stage = stages.get(i - 1);
				if(stage != null && stage != primaryStage)
				{
					LOGGER.fine("Closing stage #" + i + ": " + stage.getTitle());
					stage.close();
				}
			}
		    
		    logout();
		});
	}
	
	/**
	 * Callback that is called when the idle monitor is interrupted because of user activity.
	 */
	private void onIdleInterrupt()
	{
		if(idleNotifier.isShowing()) idleNotifier.hide();
	}
	
	/**
	 * Sets the remaining seconds in the text of the idle warning message
	 *
	 * @param remainingSeconds the remaining seconds until the session timeout
	 */
	private void setIdleWarningRemainingSeconds(int remainingSeconds)
	{
		String sSeconds;
		if(remainingSeconds == 1) sSeconds = stringsBundle.getString("label.second");
		else if(remainingSeconds == 2) sSeconds = stringsBundle.getString("label.seconds2");
		else if(remainingSeconds >= 3 && remainingSeconds <= 10)
		{
			sSeconds = String.format(stringsBundle.getString("label.seconds3To10"), remainingSeconds);
		}
		else sSeconds = String.format(stringsBundle.getString("label.seconds10Plus"), remainingSeconds);
		
		String message = String.format(stringsBundle.getString("idle.warning"), sSeconds);
		idleNotifier.setText(message);
	}
}