package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.interfaces.BodyFxController;
import sa.gov.nic.bio.bw.client.core.interfaces.ControllerResourcesLocator;
import sa.gov.nic.bio.bw.client.core.interfaces.IdleMonitorRegisterer;
import sa.gov.nic.bio.bw.client.core.interfaces.PersistableEntity;
import sa.gov.nic.bio.bw.client.core.interfaces.ResourceBundleCollection;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.IdleMonitor;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.wizard.WizardPane;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
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
 * @since 1.0.0
 */
public class CoreFxController implements IdleMonitorRegisterer, PersistableEntity
{
	private static final Logger LOGGER = Logger.getLogger(CoreFxController.class.getName());
	public static final String FXML_FILE = "sa/gov/nic/bio/bw/client/core/fxml/core.fxml";
	public static final String APP_ICON_FILE = "sa/gov/nic/bio/bw/client/core/images/app_icon.png";
	public static final String RB_LABELS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/labels";
	public static final String RB_ERRORS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/errors";
	public static final String RB_MESSAGES_FILE = "sa/gov/nic/bio/bw/client/core/bundles/messages";
	public static final String RB_TOP_MENUS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/top_menus";
	
	@FXML private Stage primaryStage;
	@FXML private StackPane overlayPane;
	@FXML private HeaderPaneFxController headerPaneController;
	@FXML private FooterPaneFxController footerPaneController;
	@FXML private MenuPaneFxController menuPaneController;
	@FXML private NotificationPane idleNotifier;
	@FXML private NotificationPane notificationPane;
	@FXML private BorderPane bodyPane;
	@FXML private WizardPane wizardPane;
	
	private ResourceBundle labelsBundle;
	private ResourceBundle errorsBundle;
	private ResourceBundle messagesBundle;
	private ResourceBundle topMenusBundle;
	private Image appIcon;
	private String windowTitle;
	
	private IdleMonitor idleMonitor;
	private GuiLanguage currentLanguage;
	private BodyFxController currentBodyController;
	
	public void passInitialResources(ResourceBundle labelsBundle, ResourceBundle errorsBundle,
	                                 ResourceBundle messagesBundle, ResourceBundle topMenusBundle,
	                                 Image appIcon, String windowTitle, GuiLanguage language)
	{
		this.labelsBundle = labelsBundle;
		this.errorsBundle = errorsBundle;
		this.messagesBundle = messagesBundle;
		this.topMenusBundle = topMenusBundle;
		this.appIcon = appIcon;
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
	public ResourceBundle getErrorsBundle(){return errorsBundle;}
	public ResourceBundle getMessagesBundle(){return messagesBundle;}
	public ResourceBundle getTopMenusBundle(){return topMenusBundle;}
	
	/**
	 * Called once for every primary stage, when it is shown. It is called in the UI-thread.
	 */
	@FXML
	private void onStageShown()
	{
		// attach this controller to the sub-controllers.
		headerPaneController.attachCoreFxController(this);
		footerPaneController.attachCoreFxController(this);
		
		// fix the size of the header pane and the menu pane.
		headerPaneController.getRootPane().setMinSize(headerPaneController.getRootPane().getWidth(),
		                                              headerPaneController.getRootPane().getHeight());
		menuPaneController.getRootPane().setMinSize(menuPaneController.getRootPane().getWidth(),
		                                            menuPaneController.getRootPane().getHeight());
		
		overlayPane.setVisible(false);
		primaryStage.setTitle(windowTitle);
		idleMonitor = new IdleMonitor(this::onShowingIdleWarning, this::onIdle, this::onIdleInterrupt,
		                              this::onTick, idleNotifier);
		
		if(currentBodyController == null) // if this is the first load, start the code workflow.
		{
			Runnable runnable = () -> Context.getWorkflowManager().startProcess(this::renderBodyForm);
			Context.getExecutorService().execute(runnable);
		}
	}
	
	/**
	 * Called when the user is submitted the form. It sends the form data to the workflow manager.
	 *
	 * @param uiDataMap the data submitted by the user when filling the form
	 */
	public void submitFormTask(Map<String, Object> uiDataMap)
	{
		Runnable runnable = () -> Context.getWorkflowManager().submitFormTask(uiDataMap);
		Context.getExecutorService().execute(runnable);
	}
	
	/*public void goToMenu(String menuId)
	{
		Map<String, Object> variables = new HashMap<>();
		variables.put("menuId", menuId);
		
		Runnable runnable = () -> Context.getWorkflowManager().raiseSignalEvent("menuSelection", variables,
																				this::renderNewBodyForm);
		Context.getExecutorService().execute(runnable);
	}
	
	public void logout()
	{
		Runnable runnable = () -> Context.getWorkflowManager().raiseSignalEvent("logout", null, this::renderNewBodyForm);
		Context.getExecutorService().execute(runnable);
	}*/
	
	/**
	 * A callback that is called by the workflow manager to render the body form. This method is called from
	 * a non-UI thread.
	 *
	 * @param controllerClass class of the new/existing controller class of the body
	 * @param dataMap data passed by the workflow manager to the controller
	 */
	private void renderBodyForm(Class<?> controllerClass, Map<String, Object> dataMap)
	{
		Platform.runLater(() ->
		{
			if(currentBodyController != null && currentBodyController.getClass() == controllerClass) // same form
			{
				currentBodyController.onReturnFromServiceTask(false, dataMap);
			}
			else // new form
			{
				try
				{
					ControllerResourcesLocator controllerResourcesLocator = (ControllerResourcesLocator)
																controllerClass.getDeclaredConstructor().newInstance();
					currentBodyController = renderNewBodyForm(controllerResourcesLocator, dataMap);
					if(currentBodyController != null)
					{
						currentBodyController.onReturnFromServiceTask(true, dataMap);
					}
				}
				catch(InstantiationException | IllegalAccessException | NoSuchMethodException |
					  InvocationTargetException e)
				{
					String errorCode = "C002-00001";
					showErrorDialogAndWaitForCore(errorCode, e, controllerClass.getName());
				}
			}
		});
	}
	
	/**
	 * Render the body form by first locating its FXML file and its resource bundles and load them. Next, replace
	 * the existing body node with the created node.
	 *
	 * @param controllerResourcesLocator a locator for the FXML file and the resource bundles
	 * @param dataMap the data passed from the workflow to the controller. It can be <code>null</code>
	 *
	 * @return the created JavaFX controller
	 */
	private BodyFxController renderNewBodyForm(ControllerResourcesLocator controllerResourcesLocator,
	                                           Map<String, Object> dataMap)
	{
		notificationPane.hide();
		
		URL fxmlUrl = controllerResourcesLocator.getFxmlLocation();
		if(fxmlUrl == null)
		{
			String errorCode = "C002-00002";
			showErrorDialogAndWaitForCore(errorCode, null, controllerResourcesLocator.getClass().getName());
			return null;
		}
		
		ResourceBundleCollection resourceBundleCollection = controllerResourcesLocator.getResourceBundleCollection();
		
		ResourceBundle labelsBundle;
		try
		{
			labelsBundle = ResourceBundle.getBundle(resourceBundleCollection.getLabelsBundlePath(),
			                                        currentLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00003";
			showErrorDialogAndWaitForCore(errorCode, e, controllerResourcesLocator.getClass().getName());
			return null;
		}
		
		ResourceBundle errorsBundle;
		try
		{
			errorsBundle = ResourceBundle.getBundle(resourceBundleCollection.getErrorsBundlePath(),
			                                        currentLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00004";
			showErrorDialogAndWaitForCore(errorCode, e, controllerResourcesLocator.getClass().getName());
			return null;
		}
		
		ResourceBundle messagesBundle;
		try
		{
			messagesBundle = ResourceBundle.getBundle(resourceBundleCollection.getMessagesBundlePath(),
			                                          currentLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00005";
			showErrorDialogAndWaitForCore(errorCode, e, controllerResourcesLocator.getClass().getName());
			return null;
		}
		
		FXMLLoader paneLoader = new FXMLLoader(fxmlUrl, labelsBundle);
		
		Node loadedPane;
		try
		{
			loadedPane = paneLoader.load();
		}
		catch(IOException e)
		{
			String errorCode = "C002-00006";
			showErrorDialogAndWaitForCore(errorCode, e, controllerResourcesLocator.getClass().getName());
			return null;
		}
		
		BodyFxController bodyFxController = paneLoader.getController();
		bodyFxController.attachCoreFxController(this);
		bodyFxController.attachInitialResources(labelsBundle, errorsBundle, messagesBundle, appIcon);
		
		bodyPane.setCenter(loadedPane);
		
		if(bodyFxController instanceof WizardStepFxControllerBase)
		{
			if(wizardPane == null)
			{
				URL wizardFxmlLocation = ((WizardStepFxControllerBase) bodyFxController).getWizardFxmlLocation();
				FXMLLoader wizardPaneLoader = new FXMLLoader(wizardFxmlLocation, labelsBundle);
				
				try
				{
					wizardPane = wizardPaneLoader.load();
				}
				catch(IOException e)
				{
					String errorCode = "C002-00019";
					showErrorDialogAndWaitForCore(errorCode, e, bodyFxController.getClass().getName());
					return null;
				}
				
				wizardPane.goNext();
			}
			else
			{
				/*String direction = (String) inputData.get("direction");
				if("backward".equals(direction)) wizardPane.goPrevious();
				else if("forward".equals(direction)) wizardPane.goNext();
				else if("startOver".equals(direction)) wizardPane.startOver();*/
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
		String title = labelsBundle.getString("dialog.confirm.title");
		String buttonConfirmText = labelsBundle.getString("dialog.confirm.buttons.confirm");
		String buttonCancelText = labelsBundle.getString("dialog.confirm.buttons.cancel");
		boolean rtl = currentLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		return DialogUtils.showConfirmationDialog(primaryStage, this, appIcon, title, headerText,
		                                          contentMessage, buttonConfirmText, buttonCancelText, rtl);
	}
	
	public void showErrorDialogAndWait(String guiErrorMessage, Exception exception)
	{
		Platform.runLater(() ->
		{
			String title = labelsBundle.getString("dialog.error.title");
			String headerText = labelsBundle.getString("dialog.error.header");
			String buttonOkText = labelsBundle.getString("dialog.error.buttons.ok");
			String moreDetailsText = labelsBundle.getString("dialog.error.buttons.showErrorDetails");
			String lessDetailsText = labelsBundle.getString("dialog.error.buttons.hideErrorDetails");
			boolean rtl = currentLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			
			DialogUtils.showErrorDialog(primaryStage, this, appIcon, title, headerText,
			                            guiErrorMessage, buttonOkText, moreDetailsText, lessDetailsText, exception,
			                            rtl);
		});
	}
	
	private void showErrorDialogAndWaitForCore(String errorCode, Exception exception, String... additionalErrorText)
	{
		String logErrorText = String.format(errorsBundle.getString(errorCode + ".internal"),
		                                    (Object[]) additionalErrorText);
		LOGGER.log(Level.SEVERE, logErrorText, exception);
		
		String guiErrorText = String.format(errorsBundle.getString(errorCode), (Object[]) additionalErrorText);
		showErrorDialogAndWait(guiErrorText, exception);
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
		
		ResourceBundle labelsBundle;
		try
		{
			labelsBundle = ResourceBundle.getBundle(RB_LABELS_FILE, toLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00008";
			showErrorDialogAndWaitForCore(errorCode, e);
			return;
		}
		
		ResourceBundle errorsBundle;
		try
		{
			errorsBundle = ResourceBundle.getBundle(RB_ERRORS_FILE, toLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00009";
			showErrorDialogAndWaitForCore(errorCode, e);
			return;
		}
		
		ResourceBundle messagesBundle;
		try
		{
			messagesBundle = ResourceBundle.getBundle(RB_MESSAGES_FILE, toLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00010";
			showErrorDialogAndWaitForCore(errorCode, e);
			return;
		}
		
		ResourceBundle topMenusBundle;
		try
		{
			topMenusBundle = ResourceBundle.getBundle(RB_TOP_MENUS_FILE, toLanguage.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00020";
			showErrorDialogAndWaitForCore(errorCode, e);
			return;
		}
		
		URL fxmlUrl = Thread.currentThread().getContextClassLoader().getResource(FXML_FILE);
		if(fxmlUrl == null)
		{
			String errorCode = "C002-00011";
			showErrorDialogAndWaitForCore(errorCode, null);
			return;
		}
		
		String version = Context.getConfigManager().getProperty("app.version");
		String title = labelsBundle.getString("window.title") + " " + version + " (" +
					   labelsBundle.getString("label.environment." +
							                  Context.getRuntimeEnvironment().name().toLowerCase()) + ")";
		windowTitle = AppUtils.replaceNumbersOnly(title, Locale.getDefault());
		
		FXMLLoader newStageLoader = new FXMLLoader(fxmlUrl, labelsBundle);
		
		Stage oldStage = primaryStage;
		
		Stage newStage;
		try
		{
			newStage = newStageLoader.load();
		}
		catch(IOException e)
		{
			String errorCode = "C002-00012";
			showErrorDialogAndWaitForCore(errorCode, e);
			return;
		}
		
		newStage.getScene().setNodeOrientation(toLanguage.getNodeOrientation());
		
		StateBundle oldState = new StateBundle();
		persistableEntity.onSaveState(oldState);
		this.onSaveState(oldState);
		
		
		CoreFxController newCoreFxController = newStageLoader.getController();
		newStage.setOnCloseRequest(GuiUtils.createOnExitHandler(primaryStage, newCoreFxController));
		
		newCoreFxController.currentBodyController = currentBodyController;
		newCoreFxController. passInitialResources(labelsBundle, errorsBundle, messagesBundle, topMenusBundle,
		                                          appIcon, windowTitle, toLanguage);
		
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
		if(currentBodyController instanceof PersistableEntity) // should always be true
		{
			BodyFxController newBodyController = renderNewBodyForm(currentBodyController, null);
			currentBodyController = newBodyController;
			
			if(newBodyController instanceof PersistableEntity) // should always be true
			{
				((PersistableEntity) newBodyController).onLoadState(stateBundle);
				this.onLoadState(stateBundle);
			}
			else // should never happen
			{
				if(newBodyController == null) LOGGER.severe("newBodyController = null");
				else LOGGER.severe("newBodyController type = " + newBodyController.getClass().getName());
				
				String errorCode = "C002-00007";
				showErrorDialogAndWaitForCore(errorCode, null);
				return false; // no success
			}
		}
		else // should never happen
		{
			if(currentBodyController == null) LOGGER.severe("currentBodyController = null");
			else LOGGER.severe("currentBodyController type = " + currentBodyController.getClass().getName());
			
			String errorCode = "C002-00013";
			showErrorDialogAndWaitForCore(errorCode, null);
			return false; // no success
		}
		
		return true; // success
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
	 * Callback that is called when the session is timed out because of user inactive
	 */
	private void onIdle()
	{
		idleNotifier.hide();
		
		Platform.runLater(() ->
		{
		    String title = labelsBundle.getString("dialog.idle.title");
		    String contentText = labelsBundle.getString("dialog.idle.content");
		    String buttonText = labelsBundle.getString("dialog.idle.buttons.goToLogin");
		    boolean rtl = currentLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		    overlayPane.setVisible(true);
		    DialogUtils.showWarningDialog(primaryStage, this, appIcon, title, null,
		                                  contentText, buttonText, rtl);
		    overlayPane.setVisible(false);
		    headerPaneController.logout();
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
		if(remainingSeconds == 1) sSeconds = labelsBundle.getString("label.second");
		else if(remainingSeconds == 2) sSeconds = labelsBundle.getString("label.seconds2");
		else if(remainingSeconds >= 3 && remainingSeconds <= 10)
		{
			sSeconds = String.format(labelsBundle.getString("label.seconds3To10"), remainingSeconds);
		}
		else sSeconds = String.format(labelsBundle.getString("label.seconds10Plus"), remainingSeconds);
		
		String message = String.format(messagesBundle.getString("idle.warning"), sSeconds);
		idleNotifier.setText(message);
	}
}