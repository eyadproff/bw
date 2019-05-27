package sa.gov.nic.bio.bw.core.controllers;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.StateBundle;
import sa.gov.nic.bio.bw.core.interfaces.IdleMonitorRegisterer;
import sa.gov.nic.bio.bw.core.interfaces.PersistableEntity;
import sa.gov.nic.bio.bw.core.tasks.LogoutTask;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.CombinedResourceBundle;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.utils.IdleMonitor;
import sa.gov.nic.bio.bw.core.wizard.WizardPane;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SignalType;
import sa.gov.nic.bio.bw.core.workflow.Workflow;
import sa.gov.nic.bio.bw.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.core.workflow.WorkflowManager;
import sa.gov.nic.bio.commons.TaskResponse;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.prefs.Preferences;

/**
 * The JavaFX controller of the primary stage, i.e. controller of the whole application GUI.
 *
 * @author Fouad Almalki
 */
public class CoreFxController extends FxControllerBase implements IdleMonitorRegisterer, PersistableEntity
{
	@FXML private Pane headerPane;
	@FXML private Pane menuPane;
	@FXML private Pane devicesRunnerGadgetPane;
	@FXML private Pane footerPane;
	@FXML private Pane sidePane;
	
	@FXML private Pane paneStageOverlay;
	@FXML private Pane paneTransitionOverlay;
	@FXML private HeaderPaneFxController headerPaneController;
	@FXML private FooterPaneFxController footerPaneController;
	@FXML private MenuPaneFxController menuPaneController;
	@FXML private DevicesRunnerGadgetPaneFxController devicesRunnerGadgetPaneController;
	@FXML private NotificationPane idleNotifier;
	@FXML private NotificationPane notificationPane;
	@FXML private BorderPane bodyPane;
	@FXML private Pane wizardPaneContainer;
	
	private Stage stage;
	private WizardPane wizardPane;
	private IdleMonitor idleMonitor;
	private BodyFxControllerBase currentBodyController;
	private CombinedResourceBundle currentBodyResourceBundle;
	private boolean newMenuSelected;
	private boolean languageChanged;
	
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
	
	public void reattachDeviceRunnerGadgetPane(){sidePane.getChildren().add(devicesRunnerGadgetPane);}
	
	public void showMockTasksCheckBox()
	{
		LOGGER.info("Showing \"mock tasks\"...");
		headerPaneController.getMockTasksCheckBox().setVisible(true);
		footerPaneController.getMockTasksCheckBox().setVisible(true);
	}
	
	public boolean isMockTasksEnabled()
	{
		return headerPaneController.getMockTasksCheckBox().isSelected();
	}
	
	@Override
	protected void initialize()
	{
		headerPaneController.getMockTasksCheckBox().selectedProperty().bindBidirectional(
														footerPaneController.getMockTasksCheckBox().selectedProperty());
		
		Context.setCoreFxController(this);
		
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
		{
			String errorCode = CoreErrorCodes.C002_00001.getCode();
			String[] errorDetails = {"Uncaught exception!"};
			showErrorDialog(errorCode, throwable, errorDetails);
		});
		
		idleMonitor = new IdleMonitor(this::onShowingIdleWarning, this::onIdle, this::onIdleInterrupt,
		                              this::onTick, idleNotifier);
		
		if(!Context.getWorkflowManager().isRunning())
		{
			Platform.runLater(() -> Context.getWorkflowManager().startCoreWorkflow(this::renderBodyForm));
		}
	}
	
	public void prepareToLogout()
	{
		menuPaneController.clearSelection();
		notificationPane.hide();
		stopIdleMonitor();
		Context.getWebserviceManager().cancelRefreshTokenScheduler();
	}
	
	public void logout()
	{
		GuiUtils.showNode(menuPane, false);
		GuiUtils.showNode(headerPane, false);
		GuiUtils.showNode(devicesRunnerGadgetPane, false);
		GuiUtils.showNode(footerPane, true);
		clearWizardBar();
		showTransitionOverlay(true);
		menuPaneController.emptyMenus();
		
		final BodyFxControllerBase controller = currentBodyController;
		if(controller != null)
		{
			controller.detach();
			Platform.runLater(controller::onDetachingFromScene);
		}
		
		Context.getWorkflowManager().interruptCurrentWorkflow(new Signal(SignalType.LOGOUT));
	}
	
	public void goToMenu(Class<?> menuWorkflowClass)
	{
		clearWizardBar();
		showTransitionOverlay(true);
		
		final BodyFxControllerBase controller = currentBodyController;
		if(controller != null)
		{
			controller.detach();
			Platform.runLater(controller::onDetachingFromScene);
		}
		
		newMenuSelected = true;
		Map<String, Object> payload = new HashMap<>();
		payload.put(Workflow.KEY_MENU_WORKFLOW_CLASS, menuWorkflowClass);
		Context.getWorkflowManager().interruptCurrentWorkflow(new Signal(SignalType.MENU_NAVIGATION, payload));
	}
	
	/**
	 * A callback that is called by the workflow manager to render the body form. This method is called from
	 * a non-UI thread.
	 *
	 * @param controllerClass class of the new/existing controller class of the body
	 * @param uiInputData data passed by the workflow manager to the controller
	 */
	private BodyFxControllerBase renderBodyForm(Class<?> controllerClass, Map<String, Object> uiInputData) throws Signal
	{
		synchronized(CoreFxController.class)
		{
			try
			{
				if(!languageChanged && !newMenuSelected && currentBodyController != null &&
																currentBodyController.getClass() == controllerClass)
				{
					// same form
					final BodyFxControllerBase controller = currentBodyController;
					
					if(!controller.isDetached())
					{
						TaskResponse<?> negativeTaskResponse = (TaskResponse<?>)
														uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
						
						if(negativeTaskResponse != null)
						{
							uiInputData.put(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, null);
							Platform.runLater(() ->
							{
								controller.onShowingProgress(false);
								controller.onReturnFromWorkflow(false);
								controller.reportNegativeTaskResponse(negativeTaskResponse.getErrorCode(),
								                                      negativeTaskResponse.getException(),
								                                      negativeTaskResponse.getErrorDetails());
							});
						}
						else
						{
							Workflow.loadWorkflowInputs(controller, uiInputData, true, true);
							Platform.runLater(() ->
							{
								controller.onShowingProgress(false);
								controller.onReturnFromWorkflow(true);
							});
						}
					}
					
				}
				else // new form
				{
					newMenuSelected = false;
					languageChanged = false;
					
					final BodyFxControllerBase oldController = currentBodyController;
					if(oldController != null)
					{
						oldController.detach();
						Platform.runLater(oldController::onDetachingFromScene);
					}
					
					String moduleName = controllerClass.getModule().getName();
					currentBodyResourceBundle = Context.getStringsResourceBundle();
					currentBodyResourceBundle.setCurrentResourceBundleProviderModule(moduleName);
					
					final BodyFxControllerBase newController = renderNewBodyForm(currentBodyResourceBundle,
					                                                             controllerClass);
					if(newController != null) Workflow.loadWorkflowInputs(newController, uiInputData,
					                                                          true, false);
					
					currentBodyController = newController;
					
					Platform.runLater(() ->
					{
						try
						{
							if(newController != null && !newController.isDetached()) newController.onAttachedToScene();
						}
						finally
						{
							showTransitionOverlay(false);
						}
					});
					
				}
			}
			catch(Exception e)
			{
				String errorCode = CoreErrorCodes.C002_00002.getCode();
				String[] errorDetails = {"Failed to render the UI! controllerClass = " + controllerClass.getName()};
				showErrorDialog(errorCode, e, errorDetails);
			}
		}
		
		return currentBodyController;
	}
	
	/**
	 * Render the body form by first locating its FXML file and its resource bundles and load them. Next, replace
	 * the existing body node with the created node.
	 *
	 * @return the created JavaFX controller
	 */
	private BodyFxControllerBase renderNewBodyForm(ResourceBundle stringsBundle,
	                                               Class<?> controllerClass)
	{
		FxmlFile fxmlFile = controllerClass.getAnnotation(FxmlFile.class);
		if(fxmlFile == null)
		{
			String errorCode = CoreErrorCodes.C002_00003.getCode();
			String[] errorDetails = {"\"@FxmlFile\" is not set!", "controllerClass = " + controllerClass};
			showErrorDialog(errorCode, null, errorDetails);
			return null;
		}
		
		String packageName = controllerClass.getPackage().getName().replace('.', '/');
		String parentPackageName = packageName.substring(0, packageName.lastIndexOf('/'));
		String fxmlPath = "/" + parentPackageName + "/fxml/" + fxmlFile.value();
		URL fxmlUrl = controllerClass.getResource(fxmlPath);
		if(fxmlUrl == null)
		{
			String errorCode = CoreErrorCodes.C002_00004.getCode();
			String[] errorDetails = {"\"fxmlUrl\" is null!", "controllerClass = " + controllerClass, "fxml path = " +
									fxmlPath};
			showErrorDialog(errorCode, null, errorDetails);
			return null;
		}
		
		FXMLLoader paneLoader = new FXMLLoader(fxmlUrl, stringsBundle);
		
		Node loadedPane;
		try
		{
			loadedPane = paneLoader.load();
			FxControllerBase fxController = paneLoader.getController();
			Platform.runLater(() -> fxController.postInitialization(paneLoader.getRoot()));
		}
		catch(IOException e)
		{
			String errorCode = CoreErrorCodes.C002_00005.getCode();
			String[] errorDetails = {"Failed to load FXML correctly!", "controllerClass = " + controllerClass};
			showErrorDialog(errorCode, e, errorDetails);
			return null;
		}
		
		BodyFxControllerBase bodyFxController = paneLoader.getController();
		Platform.runLater(() ->
		{
			notificationPane.hide();
			bodyPane.setCenter(loadedPane);
			bodyPane.applyCss();
			bodyPane.layout();
		});
		
		return bodyFxController;
	}
	
	public void showTransitionOverlay(boolean bShow)
	{
		paneTransitionOverlay.setVisible(bShow);
		if(bShow) bodyPane.setCenter(null);
	}
	
	public void setWizardPane(WizardPane wizardPane)
	{
		this.wizardPane = wizardPane;
		Platform.runLater(() -> wizardPaneContainer.getChildren().setAll(wizardPane));
	}
	
	public void clearWizardBar()
	{
		Platform.runLater(() -> wizardPaneContainer.getChildren().clear());
	}
	
	public void moveWizardForward()
	{
		Platform.runLater(() ->
		{
		    if(wizardPane != null) wizardPane.goNext();
		});
	}
	
	public void moveWizardBackward()
	{
		Platform.runLater(() ->
		{
		    if(wizardPane != null) wizardPane.goPrevious();
		});
	}
	
	public void moveWizardToTheBeginning()
	{
		Platform.runLater(() ->
		{
		    if(wizardPane != null) wizardPane.startOver();
		});
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
		WorkflowManager workflowManager = Context.getWorkflowManager();
		WorkflowBase currentWorkflow = (WorkflowBase) workflowManager.getCurrentWorkflow();
		
		Integer workflowId = null;
		Long workflowTcn = null;
		
		if(currentWorkflow != null)
		{
			workflowId = currentWorkflow.getWorkflowId();
			workflowTcn = currentWorkflow.getWorkflowTcn();
		}
		
		Integer finalWorkflowId = workflowId;
		Long finalWorkflowTcn = workflowTcn;
		Platform.runLater(() ->
		{
			List<BufferedImage> screenshots = GuiUtils.takeScreenshotsOfAllWindows();
			Context.getExecutorService().submit(() ->
			{
				try
				{
					
					
					AppUtils.reportClientError(screenshots, errorCode, errorDetails != null ?
										Arrays.toString(errorDetails) : null, AppUtils.stacktraceToString(throwable),
			                            finalWorkflowId, finalWorkflowTcn);
				}
				catch(Throwable t)
				{
					LOGGER.log(Level.WARNING, "Failed to report the client error to the server!", t);
				}
			});
			
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
		
		ResourceBundle stringsBundle;
		try
		{
			stringsBundle = AppUtils.getCoreStringsResourceBundle(toLanguage.getLocale());
		}
		catch(MissingResourceException e)
		{
			String errorCode = CoreErrorCodes.C002_00006.getCode();
			String[] errorDetails = {"Core \"stringsBundle\" resource bundle is missing!"};
			showErrorDialog(errorCode, e, errorDetails);
			return;
		}
		
		URL fxmlUrl = AppUtils.getCoreFxmlFileAsResource();
		if(fxmlUrl == null)
		{
			String errorCode = CoreErrorCodes.C002_00007.getCode();
			String[] errorDetails = {"Core \"fxmlUrl\" is null!"};
			showErrorDialog(errorCode, null, errorDetails);
			return;
		}
		
		String appVersion = Context.getAppVersion();
		String title = stringsBundle.getString("window.title") + " " + appVersion + " (" +
					   stringsBundle.getString("label.environment." +
							                  Context.getRuntimeEnvironment().name().toLowerCase()) + ")";
		String windowTitle = AppUtils.localizeNumbers(title, Locale.getDefault(), false);
		FXMLLoader newRootPane = new FXMLLoader(fxmlUrl, stringsBundle);
		Stage oldStage = stage;
		Pane rootPane;
		
		try
		{
			rootPane = newRootPane.load();
		}
		catch(IOException e)
		{
			String errorCode = CoreErrorCodes.C002_00008.getCode();
			String[] errorDetails = {"Failed to load core FXML correctly!"};
			showErrorDialog(errorCode, e, errorDetails);
			return;
		}
		
		Stage newStage = new Stage();
		
		Scene scene = new Scene(rootPane);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if(AppConstants.SHOWING_MOCK_TASKS_KEY_COMBINATION.match(event))
			{
				showMockTasksCheckBox();
				event.consume();
			}
			else if(AppConstants.SCENIC_VIEW_KEY_COMBINATION.match(event))
			{
				AppUtils.showScenicView(scene);
				event.consume();
			}
			else if(AppConstants.OPEN_APP_FOLDER_KEY_COMBINATION.match(event))
			{
				AppUtils.openAppFolder();
				event.consume();
			}
			else if(AppConstants.CHANGING_SERVER_KEY_COMBINATION.match(event))
			{
				AppUtils.showChangeServerDialog();
				event.consume();
			}
		});
		
		newStage.getIcons().setAll(oldStage.getIcons());
		newStage.setTitle(windowTitle);
		newStage.setWidth(AppConstants.STAGE_WIDTH);
		newStage.setHeight(AppConstants.STAGE_HEIGHT);
		newStage.setScene(scene);
		newStage.getScene().setNodeOrientation(toLanguage.getNodeOrientation());
		
		StateBundle oldState = new StateBundle();
		persistableEntity.onSaveState(oldState);
		this.onSaveState(oldState);
		
		CoreFxController newCoreFxController = newRootPane.getController();
		newStage.setOnCloseRequest(GuiUtils.createOnExitHandler(stage, newCoreFxController, new LogoutTask()));
		
		newCoreFxController.currentBodyController = currentBodyController; // to get its class info only
		String moduleName = currentBodyController.getClass().getModule().getName();
		newCoreFxController.currentBodyResourceBundle = Context.getStringsResourceBundle();
		newCoreFxController.currentBodyResourceBundle.reload(toLanguage.getLocale());
		newCoreFxController.currentBodyResourceBundle.setCurrentResourceBundleProviderModule(moduleName);
		newCoreFxController.registerStage(newStage);
		Context.getWorkflowManager().setFormRenderer(newCoreFxController::renderBodyForm);
		newCoreFxController.languageChanged = true;
		
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
		BodyFxControllerBase newBodyController = renderNewBodyForm(currentBodyResourceBundle,
		                                                           currentBodyController.getClass());
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
		stateBundle.putData("mockTasksEnabled", isMockTasksEnabled());
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
		boolean mockTasksEnabled = stateBundle.getDate("mockTasksEnabled", Boolean.class);
		
		if(maximized) stage.setMaximized(true);
		else
		{
			stage.setWidth(width);
			stage.setHeight(height);
			stage.setX(x);
			stage.setY(y);
		}
		
		if(mockTasksEnabled) headerPaneController.getMockTasksCheckBox().setSelected(true);
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
		LOGGER.info("The user is idle!");
		
		idleNotifier.hide();
		prepareToLogout();
		
		Platform.runLater(() ->
		{
		    String title = resources.getString("dialog.idle.title");
		    String contentText = resources.getString("dialog.idle.content");
		    String buttonText = resources.getString("dialog.idle.buttons.goToLogin");
		    boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		    paneStageOverlay.setVisible(true);
			
			// make sure the stage is not iconified, otherwise the dialog will be invisible
			stage.setIconified(false);
		    DialogUtils.showWarningDialog(stage, this, title, null,
		                                  contentText, buttonText, rtl);
		    paneStageOverlay.setVisible(false);
		    clearWizardBar();
			
			// close all opened dialogs (except the primary one)
			List<Window> windows = Window.getWindows();
			for(int i = 1; i <= windows.size(); i++)
			{
				Stage stage = (Stage) windows.get(i - 1);
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