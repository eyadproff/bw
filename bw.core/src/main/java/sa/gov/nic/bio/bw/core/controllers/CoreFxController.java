package sa.gov.nic.bio.bw.core.controllers;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.glyphfont.FontAwesome;
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
import sa.gov.nic.bio.bw.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.core.webservice.WebserviceManager;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.prefs.Preferences;

/**
 * The JavaFX controller of the primary stage, i.e. controller of the whole application GUI.
 *
 * @author Fouad Almalki
 */
public class CoreFxController extends FxControllerBase implements IdleMonitorRegisterer, PersistableEntity
{
	private static final String FXML_BODY = "/sa/gov/nic/bio/bw/core/fxml/body.fxml";
	
	@FXML private Pane headerPane;
	@FXML private Pane menuPane;
	@FXML private Pane devicesRunnerGadgetPane;
	@FXML private Pane footerPane;
	@FXML private Pane sidePane;
	
	@FXML private Pane paneStageOverlay;
	@FXML private HeaderPaneFxController headerPaneController;
	@FXML private FooterPaneFxController footerPaneController;
	@FXML private MenuPaneFxController menuPaneController;
	@FXML private DevicesRunnerGadgetPaneFxController devicesRunnerGadgetPaneController;
	@FXML private TabPane tabPane;
	@FXML private Tab tabMain;
	@FXML private NotificationPane idleNotifier;
	
	private Map<Integer, Pane> paneTransitionOverlays = new HashMap<>();
	private Map<Integer, NotificationPane> notificationPanes = new HashMap<>();
	private Map<Integer, BorderPane> bodyPanes = new HashMap<>();
	private Map<Integer, Pane> wizardPaneContainers = new HashMap<>();
	private Map<Integer, WizardPane> wizardPanes = new HashMap<>();
	private Map<Integer, ContentFxControllerBase> currentBodyControllers = new HashMap<>();
	private Map<Integer, CombinedResourceBundle> currentBodyResourceBundles = new HashMap<>();
	private Map<Integer, Class<? extends Workflow>> tabMenuTracker = new HashMap<>(); // TODO
	
	private Stage stage;
	private IdleMonitor idleMonitor;
	private boolean newMenuSelected;
	private boolean languageChanged;
	private AtomicInteger extraTabsCount = new AtomicInteger();
	
	public void registerStage(Stage stage)
	{
		this.stage = stage;
	}
	
	public Stage getStage(){return stage;}
	public NotificationPane getNotificationPane(int index){return notificationPanes.get(index);}
	public BorderPane getBodyPane(int index){return bodyPanes.get(index);}
	
	public HeaderPaneFxController getHeaderPaneController(){return headerPaneController;}
	public FooterPaneFxController getFooterPaneController(){return footerPaneController;}
	public MenuPaneFxController getMenuPaneController(){return menuPaneController;}
	public DevicesRunnerGadgetPaneFxController getDeviceManagerGadgetPaneController()
	{return devicesRunnerGadgetPaneController;}
	
	public ResourceBundle getResourceBundle(){return resources;}
	public WizardPane getWizardPane(int index){return wizardPanes.get(index);}
	public ContentFxControllerBase getCurrentBodyController(int index){return currentBodyControllers.get(index);}
	
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
		tabMain.setUserData(0);
		
		tabPane.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) ->
		{
			int index = newValue.intValue();
			boolean lastTab = tabPane.getTabs().size() - 1 == index;
			Tab tab = tabPane.getTabs().get(index);
			
			if(lastTab)
			{
				int realIndex = extraTabsCount.incrementAndGet();
				String tabTitle = resources.getString("tab.extraTab") + " " + AppUtils.localizeNumbers(String.valueOf(realIndex));
				tab.setText(tabTitle);
				tab.setUserData(realIndex);
				tab.setClosable(true);
				tab.setGraphic(null);
				tab.setOnCloseRequest(event ->
				{
					String headerText = resources.getString("closingTab.confirmation.header");
					String contentText = String.format(resources.getString("closingTab.confirmation.message"), tabTitle);
					boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
					
					if(confirmed) clearExtraTab(realIndex);
					else event.consume();
				});
				
				Tab newPlusTab = new Tab();
				newPlusTab.setClosable(false);
				newPlusTab.setGraphic(AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.PLUS));
				tabPane.getTabs().add(newPlusTab);
				tabPane.getSelectionModel().select(tab);
				
				try
				{
					Pane mainBodyPane = loadNewBodyPane(realIndex);
					tab.setContent(mainBodyPane);
				}
				catch(IOException e)
				{
					// TODO
					e.printStackTrace();
				}
				
				Context.getWorkflowManager().startCoreWorkflow(this::renderBodyForm, realIndex);
			}
			else menuPaneController.selectMenu(tabMenuTracker.get((int) tabPane.getTabs().get(index).getUserData()));
		});
		
		KeyCombination newTabShortcut = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
		
		tabPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent ->
		{
			if(newTabShortcut.match(keyEvent) && !tabPane.getStyleClass().contains("hidden-tab-header"))
			{
				tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
				keyEvent.consume();
			}
		});
		
		KeyCombination closeTabShortcut = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
		
		tabPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent ->
		{
			if(closeTabShortcut.match(keyEvent) && !tabPane.getStyleClass().contains("hidden-tab-header"))
			{
				keyEvent.consume();
				
				Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
				
				if(selectedTab.isClosable())
				{
					String headerText = resources.getString("closingTab.confirmation.header");
					String contentText = String.format(resources.getString("closingTab.confirmation.message"), selectedTab.getText());
					boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
					
					if(confirmed) tabPane.getTabs().remove(selectedTab);
				}
			}
		});
		
		KeyCombination forwardNavigation = new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN);
		KeyCombination backwardNavigation = new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
		
		// adjust tab navigation to skip the last tab (+)
		tabPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent ->
		{
			int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
			
			if(forwardNavigation.match(keyEvent))
		    {
		    	keyEvent.consume();
			
			    if(tabPane.getTabs().size() > 2)
			    {
				    if(selectedIndex == tabPane.getTabs().size() - 2) tabPane.getSelectionModel().select(0);
				    else tabPane.getSelectionModel().selectNext();
			    }
		    }
		    else if(backwardNavigation.match(keyEvent))
		    {
			    keyEvent.consume();
			
			    if(tabPane.getTabs().size() > 2)
			    {
				    if(selectedIndex == 0) tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
				    else tabPane.getSelectionModel().selectPrevious();
			    }
		    }
		});
		
		// disable tab navigation be arrows
		tabPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent ->
		{
			if(KeyCode.UP == keyEvent.getCode()) keyEvent.consume();
			else if(KeyCode.RIGHT == keyEvent.getCode()) keyEvent.consume();
			else if(KeyCode.DOWN == keyEvent.getCode()) keyEvent.consume();
			else if(KeyCode.LEFT == keyEvent.getCode()) keyEvent.consume();
		});
		
		headerPaneController.getMockTasksCheckBox().selectedProperty().bindBidirectional(footerPaneController.getMockTasksCheckBox().selectedProperty());
		
		Context.setCoreFxController(this);
		
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
		{
			String errorCode = CoreErrorCodes.C002_00001.getCode();
			String[] errorDetails = {"Uncaught exception!"};
			showErrorDialog(errorCode, throwable, errorDetails, getCurrentTabIndex());
		});
		
		idleMonitor = new IdleMonitor(this::onShowingIdleWarning, this::onIdle, this::onIdleInterrupt,
		                              this::onTick, idleNotifier);
		
		try
		{
			Pane mainBodyPane = loadNewBodyPane(0);
			tabMain.setContent(mainBodyPane);
		}
		catch(IOException e)
		{
			// TODO
			e.printStackTrace();
		}
		
		if(!Context.getWorkflowManager().isRunning(0))
		{
			Platform.runLater(() -> Context.getWorkflowManager().startCoreWorkflow(this::renderBodyForm, 0));
		}
	}
	
	private Pane loadNewBodyPane(int index) throws IOException
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_BODY));
		Pane bodyPane = fxmlLoader.load();
		BodyFxController bodyFxController = fxmlLoader.getController();
		
		notificationPanes.put(index, bodyFxController.getNotificationPane());
		bodyPanes.put(index, bodyFxController.getBodyPane());
		wizardPaneContainers.put(index, bodyFxController.getWizardPaneContainer());
		paneTransitionOverlays.put(index, bodyFxController.getPaneTransitionOverlay());
		
		return bodyPane;
	}
	
	public int getCurrentTabIndex()
	{
		return (int) tabPane.getSelectionModel().getSelectedItem().getUserData();
	}
	
	private void clearExtraTab(int index)
	{
		paneTransitionOverlays.remove(index);
		notificationPanes.remove(index);
		bodyPanes.remove(index);
		wizardPaneContainers.remove(index);
		wizardPanes.remove(index);
		currentBodyControllers.remove(index);
		currentBodyResourceBundles.remove(index);
		tabMenuTracker.remove(index);
		Context.getWorkflowManager().interruptCoreWorkflow(index);
	}
	
	private void clearExtraTabs()
	{
		for(int i = 1; i < tabPane.getTabs().size() - 1; i++)
		{
			clearExtraTab(i);
		}
		
		extraTabsCount.set(0);
		var tabs = tabPane.getTabs();
		tabs.retainAll(tabs.get(0), tabs.get(tabs.size() - 1));
		tabPane.getSelectionModel().select(0);
		menuPaneController.clearSelection();
	}
	
	public void onLogin(boolean hasAccessToMenus)
	{
		if(hasAccessToMenus) tabPane.getStyleClass().remove("hidden-tab-header");
	}
	
	public void prepareToLogout()
	{
		stopIdleMonitor();
		Context.getWebserviceManager().cancelRefreshTokenScheduler();
	}
	
	public void logout()
	{
		for(NotificationPane notificationPane : notificationPanes.values()) notificationPane.hide();
		
		GuiUtils.showNode(menuPane, false);
		GuiUtils.showNode(headerPane, false);
		GuiUtils.showNode(devicesRunnerGadgetPane, false);
		GuiUtils.showNode(footerPane, true);
		clearWizardBar();
		showTransitionOverlay(true);
		menuPaneController.emptyMenus();
		
		for(ContentFxControllerBase currentBodyController : currentBodyControllers.values())
		{
			if(currentBodyController != null)
			{
				currentBodyController.detach();
				currentBodyController.onDetachingFromScene();
			}
		}
		
		tabPane.getStyleClass().add("hidden-tab-header");
		clearExtraTabs();
		
		Context.getWorkflowManager().interruptCurrentWorkflow(new Signal(SignalType.LOGOUT), 0);
	}
	
	public void goToMenu(Class<? extends Workflow> menuWorkflowClass)
	{
		tabMenuTracker.put(getCurrentTabIndex(), menuWorkflowClass);
		
		clearWizardBar();
		showTransitionOverlay(true);
		
		final ContentFxControllerBase controller = currentBodyControllers.get(getCurrentTabIndex());
		if(controller != null)
		{
			controller.detach();
			Platform.runLater(controller::onDetachingFromScene);
		}
		
		newMenuSelected = true;
		Map<String, Object> payload = new HashMap<>();
		payload.put(Workflow.KEY_MENU_WORKFLOW_CLASS, menuWorkflowClass);
		Context.getWorkflowManager().interruptCurrentWorkflow(new Signal(SignalType.MENU_NAVIGATION, payload), getCurrentTabIndex());
	}
	
	/**
	 * A callback that is called by the workflow manager to render the body form. This method is called from
	 * a non-UI thread.
	 *
	 * @param controllerClass class of the new/existing controller class of the body
	 * @param uiInputData data passed by the workflow manager to the controller
	 */
	private ContentFxControllerBase renderBodyForm(Class<?> controllerClass, Map<String, Object> uiInputData, int index) throws Signal
	{
		LOGGER.fine("New render request by thread (" + Thread.currentThread().getName() + " ) with controllerClass (" + controllerClass.getName() + ")");
		
		ContentFxControllerBase currentBodyController = currentBodyControllers.get(index);
		
		synchronized(CoreFxController.class)
		{
			try
			{
				if(!languageChanged && !newMenuSelected && currentBodyController != null && currentBodyController.getClass() == controllerClass /*&&
					currentBodyController.getTabIndex() == getCurrentTabIndex()*/)
				{
					// same form
					final ContentFxControllerBase controller = currentBodyController;
					
					if(!controller.isDetached())
					{
						TaskResponse<?> negativeTaskResponse = (TaskResponse<?>) uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
						
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
					
					if(currentBodyController != null)
					{
						currentBodyController.detach();
						Platform.runLater(currentBodyController::onDetachingFromScene);
					}
					
					String moduleName = controllerClass.getModule().getName();
					CombinedResourceBundle currentBodyResourceBundle = Context.getStringsResourceBundle();
					currentBodyResourceBundles.put(index, currentBodyResourceBundle);
					currentBodyResourceBundle.setCurrentResourceBundleProviderModule(moduleName);
					
					final ContentFxControllerBase newController = renderNewBodyForm(currentBodyResourceBundle,
					                                                                controllerClass, index);
					if(newController != null)
					{
						newController.setTabIndex(index);
						Workflow.loadWorkflowInputs(newController, uiInputData, true, false);
					}
					
					currentBodyController = newController;
					currentBodyControllers.put(index, newController);
					
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
				showErrorDialog(errorCode, e, errorDetails, getCurrentTabIndex());
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
	private ContentFxControllerBase renderNewBodyForm(ResourceBundle stringsBundle,
	                                                  Class<?> controllerClass, int tabIndex)
	{
		FxmlFile fxmlFile = controllerClass.getAnnotation(FxmlFile.class);
		if(fxmlFile == null)
		{
			String errorCode = CoreErrorCodes.C002_00003.getCode();
			String[] errorDetails = {"\"@FxmlFile\" is not set!", "controllerClass = " + controllerClass};
			showErrorDialog(errorCode, null, errorDetails, tabIndex);
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
			showErrorDialog(errorCode, null, errorDetails, tabIndex);
			return null;
		}
		
		FXMLLoader paneLoader = new FXMLLoader(fxmlUrl, stringsBundle);
		
		Node loadedPane;
		try
		{
			loadedPane = paneLoader.load();
			ContentFxControllerBase fxController = paneLoader.getController();
			Platform.runLater(() -> fxController.postInitialization(paneLoader.getRoot()));
		}
		catch(IOException e)
		{
			String errorCode = CoreErrorCodes.C002_00005.getCode();
			String[] errorDetails = {"Failed to load FXML correctly!", "controllerClass = " + controllerClass};
			showErrorDialog(errorCode, e, errorDetails, tabIndex);
			return null;
		}
		
		ContentFxControllerBase bodyFxController = paneLoader.getController();
		Platform.runLater(() ->
		{
			notificationPanes.get(tabIndex).hide();
			bodyPanes.get(tabIndex).setCenter(loadedPane);
			bodyPanes.get(tabIndex).applyCss();
			bodyPanes.get(tabIndex).layout();
		});
		
		return bodyFxController;
	}
	
	public void showTransitionOverlay(boolean bShow)
	{
		int index = getCurrentTabIndex();
		if(paneTransitionOverlays.get(index) != null) paneTransitionOverlays.get(index).setVisible(bShow);
		if(bShow && bodyPanes.get(index) != null) bodyPanes.get(index).setCenter(null);
	}
	
	public void setWizardPane(WizardPane wizardPane)
	{
		int index = getCurrentTabIndex();
		this.wizardPanes.put(index, wizardPane);
		Pane wizardPaneContainer = wizardPaneContainers.get(index);
		if(wizardPaneContainer != null) Platform.runLater(() -> wizardPaneContainer.getChildren().setAll(wizardPane));
	}
	
	public void clearWizardBar()
	{
		int index = getCurrentTabIndex();
		Pane wizardPaneContainer = wizardPaneContainers.get(index);
		if(wizardPaneContainer != null) Platform.runLater(() -> wizardPaneContainer.getChildren().clear());
	}
	
	public void moveWizardForward()
	{
		Platform.runLater(() ->
		{
			int index = getCurrentTabIndex();
		    if(wizardPanes.get(index) != null) wizardPanes.get(index).goNext();
		});
	}
	
	public void moveWizardBackward()
	{
		Platform.runLater(() ->
		{
			int index = getCurrentTabIndex();
		    if(wizardPanes.get(index) != null) wizardPanes.get(index).goPrevious();
		});
	}
	
	public void moveWizardToTheBeginning()
	{
		Platform.runLater(() ->
		{
			int index = getCurrentTabIndex();
		    if(wizardPanes.get(index) != null) wizardPanes.get(index).startOver();
		});
	}
	
	// must be called from UI thread
	public boolean showConfirmationDialogAndWait(String headerText, String contentMessage)
	{
		return showConfirmationDialogAndWait(stage, headerText, contentMessage);
	}
	
	public boolean showConfirmationDialogAndWait(Stage stage, String headerText, String contentMessage)
	{
		String title = resources.getString("dialog.confirm.title");
		String buttonConfirmText = resources.getString("dialog.confirm.buttons.confirm");
		String buttonCancelText = resources.getString("dialog.confirm.buttons.cancel");
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		return DialogUtils.showConfirmationDialog(stage, this, title, headerText,
		                                          contentMessage, buttonConfirmText, buttonCancelText, rtl);
	}
	
	public void showErrorDialog(String errorCode, Throwable throwable, String[] errorDetails, int tabIndex)
	{
		WorkflowManager workflowManager = Context.getWorkflowManager();
		WorkflowBase currentWorkflow = (WorkflowBase) workflowManager.getCurrentWorkflow(tabIndex);
		
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
			showErrorDialog(errorCode, e, errorDetails, getCurrentTabIndex());
			return;
		}
		
		URL fxmlUrl = AppUtils.getCoreFxmlFileAsResource();
		if(fxmlUrl == null)
		{
			String errorCode = CoreErrorCodes.C002_00007.getCode();
			String[] errorDetails = {"Core \"fxmlUrl\" is null!"};
			showErrorDialog(errorCode, null, errorDetails, getCurrentTabIndex());
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
			showErrorDialog(errorCode, e, errorDetails, getCurrentTabIndex());
			return;
		}
		
		Stage newStage = new Stage();
		
		Scene scene = new Scene(rootPane);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			RuntimeEnvironment runtimeEnvironment = Context.getRuntimeEnvironment();
			if(runtimeEnvironment == RuntimeEnvironment.LOCAL || runtimeEnvironment == RuntimeEnvironment.DEV)
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
				else if(AppConstants.CHANGING_SERVER_KEY_COMBINATION.match(event))
				{
					String[] urls = Context.getConfigManager().getProperty("dev.webservice.urls").split("[,\\s]+");
					WebserviceManager webserviceManager = Context.getWebserviceManager();
					String oldBaseUrl = webserviceManager.getServerBaseUrl();
					String userChoice = AppUtils.showChangeServerDialog(oldBaseUrl, urls, true);
					
					if(userChoice != null)
					{
						Context.getWebserviceManager().changeServerBaseUrl(userChoice);
						LOGGER.info("change the server base URL from (" + oldBaseUrl + ") to (" + userChoice + ")");
					}
					
					event.consume();
				}
			}
			
			if(AppConstants.OPEN_APP_FOLDER_KEY_COMBINATION.match(event))
			{
				AppUtils.openAppFolder();
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
		
		int index = getCurrentTabIndex();
		newCoreFxController.currentBodyControllers.put(index, currentBodyControllers.get(index)); // to get its class info only
		String moduleName = currentBodyControllers.get(index).getClass().getModule().getName();
		newCoreFxController.currentBodyResourceBundles.put(index, Context.getStringsResourceBundle());
		newCoreFxController.currentBodyResourceBundles.get(index).reload(toLanguage.getLocale());
		newCoreFxController.currentBodyResourceBundles.get(index).setCurrentResourceBundleProviderModule(moduleName);
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
		int index = getCurrentTabIndex();
		ContentFxControllerBase newBodyController = renderNewBodyForm(currentBodyResourceBundles.get(index),
		                                                              currentBodyControllers.get(index).getClass(), index);
		currentBodyControllers.put(index, newBodyController);
		
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