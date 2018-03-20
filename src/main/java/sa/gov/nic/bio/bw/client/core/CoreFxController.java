package sa.gov.nic.bio.bw.client.core;

import com.sun.javafx.stage.StageHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.interfaces.ControllerResourcesLocator;
import sa.gov.nic.bio.bw.client.core.interfaces.IdleMonitorRegisterer;
import sa.gov.nic.bio.bw.client.core.interfaces.PersistableEntity;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * The JavaFX controller of the primary stage, i.e. controller of the whole application.
 *
 * @author Fouad Almalki
 */
public class CoreFxController implements IdleMonitorRegisterer, PersistableEntity
{
	private static final Logger LOGGER = Logger.getLogger(CoreFxController.class.getName());
	public static final String FXML_FILE = "sa/gov/nic/bio/bw/client/core/fxml/core.fxml";
	public static final String APP_ICON_FILE = "sa/gov/nic/bio/bw/client/core/images/app_icon.png";
	public static final String RB_LABELS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/strings";
	
	
	// the following are here only to avoid warnings in FXML files.
	@FXML private Pane headerPane;
	@FXML private Pane menuPane;
	@FXML private Pane devicesRunnerGadgetPane;
	@FXML private Pane footerPane;
	
	@FXML private ResourceBundle resources;
	@FXML private StackPane stageOverlayPane;
	@FXML private StackPane menuTransitionOverlayPane;
	@FXML private ProgressIndicator piWizardTransition;
	@FXML private HeaderPaneFxController headerPaneController;
	@FXML private FooterPaneFxController footerPaneController;
	@FXML private MenuPaneFxController menuPaneController;
	@FXML private DevicesRunnerGadgetPaneFxController devicesRunnerGadgetPaneController;
	@FXML private NotificationPane idleNotifier;
	@FXML private NotificationPane notificationPane;
	@FXML private BorderPane bodyPane;
	@FXML private WizardPane wizardPane;
	
	private Stage stage;
	private IdleMonitor idleMonitor;
	private BodyFxControllerBase currentBodyController;
	
	public void registerStage(Stage stage)
	{
		this.stage = stage;
	}
	
	public Stage getStage(){return stage;}
	public NotificationPane getNotificationPane(){return notificationPane;}
	public BorderPane getBodyPane(){return bodyPane;}
	
	public HeaderPaneFxController getHeaderPaneController(){return headerPaneController;}
	public FooterPaneFxController getFooterPaneController(){return footerPaneController;}
	public MenuPaneFxController getMenuPaneController(){return menuPaneController;}
	public DevicesRunnerGadgetPaneFxController getDeviceManagerGadgetPaneController()
	{return devicesRunnerGadgetPaneController;}
	
	public ResourceBundle getResourceBundle(){return resources;}
	public WizardPane getWizardPane(){return wizardPane;}
	public BodyFxControllerBase getCurrentBodyController(){return currentBodyController;}
	
	@FXML
	private void initialize()
	{
		Context.setCoreFxController(this);
		
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
	
	public void prepareToLogout()
	{
		Context.getCoreFxController().getNotificationPane().hide();
		Context.getCoreFxController().stopIdleMonitor();
		Context.getWebserviceManager().cancelRefreshTokenScheduler();
		
		try
		{
			Context.getBioKitManager().disconnect();
		}
		catch(Exception e)
		{
			if(!(e instanceof NotConnectedException)) LOGGER.log(Level.WARNING,
		                                                     "failed to disconnect with Biokit on logout!", e);
		}
	}
	
	public void logout()
	{
		clearWizardBar();
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
		if(currentBodyController != null && currentBodyController.getClass() == controllerClass) // same form
		{
			Platform.runLater(() -> currentBodyController.onWorkflowUserTaskLoad(false, uiInputData));
			
		}
		else // new form
		{
			try
			{
				ControllerResourcesLocator controllerResourcesLocator = (ControllerResourcesLocator)
						controllerClass.getDeclaredConstructor().newInstance();
				currentBodyController = renderNewBodyForm(controllerResourcesLocator);
				
				if(currentBodyController != null)
				{
					Platform.runLater(() -> currentBodyController.onWorkflowUserTaskLoad(true, uiInputData));
				}
			}
			catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
			{
				String errorCode = CoreErrorCodes.C002_00001.getCode();
				String[] errorDetails = {"Failed to load the class " + controllerClass.getName()};
				showErrorDialog(errorCode, e, errorDetails);
			}
		}
	}
	
	/**
	 * Render the body form by first locating its FXML file and its resource bundles and load them. Next, replace
	 * the existing body node with the created node.
	 *
	 * @param controllerResourcesLocator a locator for the FXML file and the resource bundles
	 *
	 * @return the created JavaFX controller
	 */
	private BodyFxControllerBase renderNewBodyForm(ControllerResourcesLocator controllerResourcesLocator)
	{
		URL fxmlUrl = controllerResourcesLocator.getFxmlLocation();
		if(fxmlUrl == null)
		{
			String errorCode = CoreErrorCodes.C002_00002.getCode();
			String[] errorDetails = {"\"fxmlUrl\" is null!", "controllerClass = " +
									controllerResourcesLocator.getClass().getName()};
			showErrorDialog(errorCode, null, errorDetails);
			return null;
		}
		
		String stringsResourceBundlePath = controllerResourcesLocator.getStringsResourceBundle();
		
		ResourceBundle stringsBundle;
		try
		{
			stringsBundle = ResourceBundle.getBundle(stringsResourceBundlePath, Context.getGuiLanguage().getLocale(),
			                                         new UTF8Control());
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
		paneLoader.setClassLoader(Context.getFxClassLoader());
		
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
		
		Platform.runLater(() ->
		{
			notificationPane.hide();
			bodyFxController.onAttachedToScene();
			bodyPane.setCenter(loadedPane);
			showMenuTransitionProgressIndicator(false);
			showWizardTransitionProgressIndicator(false);
			
			if(currentBodyController != null) currentBodyController.onDetachedFromScene();
		});
		
		return bodyFxController;
	}
	
	public void showWizardTransitionProgressIndicator(boolean bShow)
	{
		piWizardTransition.setVisible(bShow);
		if(bShow) bodyPane.setCenter(null);
	}
	
	public void showMenuTransitionProgressIndicator(boolean bShow)
	{
		menuTransitionOverlayPane.setVisible(bShow);
	}
	
	public void loadWizardBar(FXMLLoader wizardPaneLoader)
	{
		try
		{
			wizardPane = wizardPaneLoader.load();
			
			Platform.runLater(() ->
			{
				bodyPane.setTop(wizardPane);
				wizardPane.goNext();
			});
		}
		catch(Exception e)
		{
			String errorCode = CoreErrorCodes.C002_00005.getCode();
			String[] errorDetails = {"Failed to load FXML correctly!", "wizardFxmlLocation = " +
																					wizardPaneLoader.getLocation()};
			showErrorDialog(errorCode, e, errorDetails);
		}
	}
	
	public void clearWizardBar()
	{
		wizardPane = null;
		bodyPane.setTop(null);
	}
	
	public void moveWizardForward()
	{
		wizardPane.goNext();
	}
	
	public void moveWizardBackward()
	{
		wizardPane.goPrevious();
	}
	
	public void moveWizardToTheBeginning()
	{
		wizardPane.startOver();
	}
	
	// must be called from UI thread
	public boolean showConfirmationDialogAndWait(String headerText, String contentMessage)
	{
		String title = resources.getString("dialog.confirm.title");
		String buttonConfirmText = resources.getString("dialog.confirm.buttons.confirm");
		String buttonCancelText = resources.getString("dialog.confirm.buttons.cancel");
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		return DialogUtils.showConfirmationDialog(stage, this, title, headerText,
		                                          contentMessage, buttonConfirmText, buttonCancelText, rtl);
	}
	
	public void showErrorDialog(String errorCode, Throwable throwable, String[] errorDetails)
	{
		Platform.runLater(() ->
		{
			String title = resources.getString("dialog.error.title");
			String headerText = resources.getString("dialog.error.header");
			String buttonOkText = resources.getString("dialog.error.buttons.ok");
			String buttonMoreDetailsText = resources.getString("dialog.error.buttons.showErrorDetails");
			String buttonLessDetailsText = resources.getString("dialog.error.buttons.hideErrorDetails");
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			
			String contentMessage = String.format(resources.getString("message.errorOccurs"), errorCode);
			
			StringBuilder sb = new StringBuilder();
			GuiUtils.buildErrorMessage(throwable, errorDetails, sb);
			
			LOGGER.severe(contentMessage + (sb.length() > 0 ? "\n" : "") + sb.toString());
			DialogUtils.showAlertDialog(AlertType.ERROR, stage, this, title, headerText,
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
		if(Context.getGuiLanguage() == toLanguage)
		{
			LOGGER.warning("cannot switch the language to the current language: " +
					                                                                Context.getGuiLanguage().name());
			return;
		}
		
		Context.setGuiLanguage(toLanguage);
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
		String windowTitle = AppUtils.replaceNumbersOnly(title, Locale.getDefault());
		
		FXMLLoader newRootPane = new FXMLLoader(fxmlUrl, stringsBundle);
		newRootPane.setClassLoader(Context.getFxClassLoader());
		
		Stage oldStage = stage;
		
		Pane rootPane;
		
		try
		{
			rootPane = newRootPane.load();
		}
		catch(IOException e)
		{
			String errorCode = CoreErrorCodes.C002_00010.getCode();
			String[] errorDetails = {"Failed to load core FXML correctly!"};
			showErrorDialog(errorCode, e, errorDetails);
			return;
		}
		
		Stage newStage = new Stage();
		
		newStage.getIcons().setAll(oldStage.getIcons());
		newStage.setTitle(windowTitle);
		newStage.setWidth(AppConstants.STAGE_WIDTH);
		newStage.setHeight(AppConstants.STAGE_HEIGHT);
		newStage.setScene(new Scene(rootPane));
		newStage.getScene().setNodeOrientation(toLanguage.getNodeOrientation());
		
		StateBundle oldState = new StateBundle();
		persistableEntity.onSaveState(oldState);
		this.onSaveState(oldState);
		
		CoreFxController newCoreFxController = newRootPane.getController();
		newStage.setOnCloseRequest(GuiUtils.createOnExitHandler(stage, newCoreFxController));
		
		newCoreFxController.currentBodyController = currentBodyController;
		newCoreFxController.registerStage(newStage);
		
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
		BodyFxControllerBase newBodyController = renderNewBodyForm(currentBodyController);
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
		stateBundle.putData("stageWidth", stage.getWidth());
		stateBundle.putData("stageHeight", stage.getHeight());
		stateBundle.putData("stageX", stage.getX());
		stateBundle.putData("stageY", stage.getY());
		stateBundle.putData("stageMaximized", stage.isMaximized());
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
		
		if(maximized) stage.setMaximized(true);
		else
		{
			stage.setWidth(width);
			stage.setHeight(height);
			stage.setX(x);
			stage.setY(y);
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
		registerStageForIdleMonitoring(stage);
		idleMonitor.startMonitoring();
	}
	
	/**
	 * Stop the idle monitoring.
	 */
	private void stopIdleMonitor()
	{
		unregisterStageForIdleMonitoring(stage);
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
		prepareToLogout();
		
		Platform.runLater(() ->
		{
		    String title = resources.getString("dialog.idle.title");
		    String contentText = resources.getString("dialog.idle.content");
		    String buttonText = resources.getString("dialog.idle.buttons.goToLogin");
		    boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		    stageOverlayPane.setVisible(true);
			
			// make sure the stage is not iconified, otherwise the dialog will be invisible
			stage.setIconified(false);
		    DialogUtils.showWarningDialog(stage, this, title, null,
		                                  contentText, buttonText, rtl);
		    stageOverlayPane.setVisible(false);
		    clearWizardBar();
			
			// close all opened dialogs (except the primary one)
			ObservableList<Stage> stages = StageHelper.getStages();
			for(int i = 1; i <= stages.size(); i++)
			{
				Stage stage = stages.get(i - 1);
				if(stage != null && stage != this.stage)
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
		if(remainingSeconds == 1) sSeconds = resources.getString("label.second");
		else if(remainingSeconds == 2) sSeconds = resources.getString("label.seconds2");
		else if(remainingSeconds >= 3 && remainingSeconds <= 10)
		{
			sSeconds = String.format(resources.getString("label.seconds3To10"), remainingSeconds);
		}
		else sSeconds = String.format(resources.getString("label.seconds10Plus"), remainingSeconds);
		
		String message = String.format(resources.getString("idle.warning"), sSeconds);
		idleNotifier.setText(message);
	}
}