package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.beans.GuiState;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.interfaces.*;
import sa.gov.nic.bio.bw.client.core.utils.*;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.webservice.RefreshTokenAPI;
import sa.gov.nic.bio.bw.client.core.webservice.RefreshTokenBean;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoreFxController
{
	private static final Logger LOGGER = Logger.getLogger(CoreFxController.class.getName());
	public static final String FXML_FILE = "sa/gov/nic/bio/bw/client/core/fxml/core.fxml";
	public static final String APP_ICON_FILE = "sa/gov/nic/bio/bw/client/core/images/app_icon.png";
	public static final String RB_LABELS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/labels";
	public static final String RB_ERRORS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/errors";
	public static final String RB_MESSAGES_FILE = "sa/gov/nic/bio/bw/client/core/bundles/messages";
	
	@FXML private Stage primaryStage;
	@FXML private StackPane overlayPane;
	@FXML private HeaderPaneFxController headerPaneController;
	@FXML private FooterPaneFxController footerPaneController;
	@FXML private MenuPaneFxController menuPaneController;
	@FXML private NotificationPane idleNotifier;
	@FXML private StackPane bodyPane;
	
	private ResourceBundle labelsBundle;
	private ResourceBundle errorsBundle;
	private ResourceBundle messagesBundle;
	private Image appIcon;
	private String windowTitle;
	private int idleWarningBeforeSeconds;
	private int idleWarningAfterSeconds;
	
	private GuiState guiState = new GuiState();
	private IdleMonitor idleMonitor;
	private ScheduledFuture<?> scheduledRefreshTokenFuture;
	private boolean idleWarningOn = false;
	
	public void passInitialResources(ResourceBundle labelsBundle, ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon, String windowTitle, int idleWarningBeforeSeconds, int idleWarningAfterSeconds)
	{
		this.labelsBundle = labelsBundle;
		this.errorsBundle = errorsBundle;
		this.messagesBundle = messagesBundle;
		this.appIcon = appIcon;
		this.windowTitle = windowTitle;
		this.idleWarningBeforeSeconds = idleWarningBeforeSeconds;
		this.idleWarningAfterSeconds = idleWarningAfterSeconds;
	}
	
	public Stage getPrimaryStage(){return primaryStage;}
	public StackPane getBodyPane(){return bodyPane;}
	public GuiState getGuiState(){return guiState;}
	
	public HeaderPaneFxController getHeaderPaneController(){return headerPaneController;}
	public FooterPaneFxController getFooterPaneController(){return footerPaneController;}
	public MenuPaneFxController getMenuPaneController(){return menuPaneController;}
	
	@FXML
	private void initialize(){}
	
	@FXML
	private void onStageShown()
	{
		headerPaneController.attachCoreFxController(this);
		footerPaneController.attachCoreFxController(this);
		menuPaneController.attachCoreFxController(this);
		
		headerPaneController.getRootPane().setMinHeight(headerPaneController.getRootPane().getHeight());
		
		if(guiState.getBodyController() == null) // if this is the first load
		{
			Runnable runnable = () -> Context.getWorkflowManager().startProcess(this::showForm);
			Context.getExecutorService().execute(runnable);
		}
		
		primaryStage.setTitle(windowTitle);
		idleMonitor = new IdleMonitor(idleWarningBeforeSeconds, this::onShowingIdleWarning, idleWarningAfterSeconds, this::onIdle, this::onIdleInterrupt, this::onTick, idleNotifier);
	}
	
	public void startIdleMonitor()
	{
		idleMonitor.register(primaryStage, Event.ANY);
		idleMonitor.startMonitoring();
		// TODO: register dialogs
	}
	
	void stopIdleMonitor()
	{
		idleMonitor.unregister(primaryStage, Event.ANY);
		idleMonitor.stopMonitoring();
	}
	
	private void onShowingIdleWarning()
	{
		idleWarningOn = true;
		idleNotifier.show();
		setIdleWarningText(idleWarningAfterSeconds);
	}
	
	private void onTick(Integer seconds)
	{
		setIdleWarningText(seconds);
	}
	
	private void setIdleWarningText(int seconds)
	{
		String sSeconds;
		if(seconds == 1) sSeconds = labelsBundle.getString("label.second");
		else if(seconds == 2) sSeconds = labelsBundle.getString("label.seconds2");
		else if(seconds >= 3 && seconds <= 10) sSeconds = String.format(labelsBundle.getString("label.seconds3To10"), seconds);
		else sSeconds = String.format(labelsBundle.getString("label.seconds10Plus"), seconds);
		
		String message = String.format(messagesBundle.getString("idle.warning"), sSeconds);
		idleNotifier.setText(message);
	}
	
	private void onIdle()
	{
		idleNotifier.hide();
		idleWarningOn = false;
		
		Platform.runLater(() ->
		{
			String title = labelsBundle.getString("dialog.idle.title");
			String contentText = labelsBundle.getString("dialog.idle.content");
			String buttonText = labelsBundle.getString("dialog.idle.buttons.goToLogin");
			boolean rtl = guiState.getLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			
			overlayPane.setVisible(true);
			DialogUtils.showWarningDialog(appIcon, title, null, contentText, buttonText, rtl);
			overlayPane.setVisible(false);
			headerPaneController.logout();
		});
	}
	
	private void onIdleInterrupt()
	{
		if(idleWarningOn)
		{
			idleWarningOn = false;
			idleNotifier.hide();
		}
	}
	
	public void scheduleRefreshToken(String userToken)
	{
		if(userToken == null) LOGGER.warning("userToken = null");
		else
		{
			LocalDateTime expiration = AppUtils.extractExpirationTimeFromJWT(userToken);
			if(expiration != null)
			{
				long seconds = Math.abs(expiration.until(LocalDateTime.now(), ChronoUnit.SECONDS));
				long delay = seconds - seconds / 10L; // the last tenth of the token lifetime
				
				scheduledRefreshTokenFuture = Context.getScheduledExecutorService().schedule(() ->
                {
                    RefreshTokenAPI refreshTokenAPI = Context.getWebserviceManager().getApi(RefreshTokenAPI.class);
	                String url = System.getProperty("jnlp.bio.bw.service.refreshToken");
                    Call<RefreshTokenBean> apiCall = refreshTokenAPI.refreshToken(url, userToken);
                    ApiResponse<RefreshTokenBean> response = Context.getWebserviceManager().executeApi(apiCall);

                    if(response.isSuccess())
                    {
                        RefreshTokenBean refreshTokenBean = response.getResult();
                        String newToken = refreshTokenBean.getUserToken();
                        Context.getUserData().getLoginBean().setUserToken(newToken);
                        scheduleRefreshToken(newToken);
                    }
                    else
                    {
                        LOGGER.warning("Failed to refresh the token! httpCode = " + response.getHttpCode() + ", errorCode = " + response.getErrorCode());
                    }
                }, delay, TimeUnit.SECONDS);
			}
		}
	}
	
	public void cancelRefreshTokenScheduler()
	{
		if(scheduledRefreshTokenFuture != null) scheduledRefreshTokenFuture.cancel(true);
	}
	
	public void submitFormTask(Map<String, String> uiDataMap)
	{
		Runnable runnable = () -> Context.getWorkflowManager().submitFormTask(uiDataMap, this::showForm);
		Context.getExecutorService().execute(runnable);
	}
	
	public void goToMenu(String menuId)
	{
		Map<String, Object> variables = new HashMap<>();
		variables.put("menuId", menuId);
		
		Runnable runnable = () -> Context.getWorkflowManager().raiseSignalEvent("menuSelection", variables, this::showForm);
		Context.getExecutorService().execute(runnable);
	}
	
	public void logout()
	{
		Runnable runnable = () -> Context.getWorkflowManager().raiseSignalEvent("logout", null, this::showForm);
		Context.getExecutorService().execute(runnable);
	}
	
	private void showForm(String formKey, Map<String, Object> inputData)
	{
		Boolean sameForm = (Boolean) inputData.get("sameForm");
		
		if(sameForm != null && sameForm)
		{
			guiState.getBodyController().onReturnFromTask(inputData);
		}
		else
		{
			BodyFxController bodyFxController;
			
			try
			{
				bodyFxController = (BodyFxController) Class.forName(formKey).getDeclaredConstructor().newInstance();
			}
			catch(InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e)
			{
				String errorCode = "C002-00001";
				showErrorDialogAndWaitForCore(errorCode, e, formKey);
				return;
			}
			
			Platform.runLater(() -> showForm(bodyFxController, inputData, guiState.getLanguage()));
		}
	}
	
	private BodyFxController showForm(BodyFxController bodyFxController, Map<String, Object> inputData, GuiLanguage language)
	{
		URL fxmlUrl = bodyFxController.getFxmlLocation();
		if(fxmlUrl == null)
		{
			String errorCode = "C002-00002";
			showErrorDialogAndWaitForCore(errorCode, null, bodyFxController.getClass().getName());
			return null;
		}
		
		ResourceBundleCollection resourceBundleCollection = bodyFxController.getResourceBundleCollection();
		
		ResourceBundle labelsBundle;
		try
		{
			labelsBundle = ResourceBundle.getBundle(resourceBundleCollection.getLabelsBundlePath(), language.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00003";
			showErrorDialogAndWaitForCore(errorCode, e, bodyFxController.getClass().getName());
			return null;
		}
		
		ResourceBundle errorsBundle;
		try
		{
			errorsBundle = ResourceBundle.getBundle(resourceBundleCollection.getErrorsBundlePath(), language.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00004";
			showErrorDialogAndWaitForCore(errorCode, e, bodyFxController.getClass().getName());
			return null;
		}
		
		ResourceBundle messagesBundle;
		try
		{
			messagesBundle = ResourceBundle.getBundle(resourceBundleCollection.getMessagesBundlePath(), language.getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00005";
			showErrorDialogAndWaitForCore(errorCode, e, bodyFxController.getClass().getName());
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
			showErrorDialogAndWaitForCore(errorCode, e, bodyFxController.getClass().getName());
			return null;
		}
		
		BodyFxController controller = paneLoader.getController();
		guiState.setBodyController(controller);
		
		controller.attachCoreFxController(this);
		controller.attachInitialResources(labelsBundle, errorsBundle, messagesBundle, appIcon);
		controller.attachInputData(inputData);
		
		bodyPane.getChildren().setAll(loadedPane);
		controller.onControllerReady();
		
		return controller;
	}
	
	// must be called from UI thread
	public boolean showConfirmationDialogAndWait(String headerText, String contentMessage)
	{
		String title = labelsBundle.getString("dialog.confirm.title");
		String buttonConfirmText = labelsBundle.getString("dialog.confirm.buttons.confirm");
		String buttonCancelText = labelsBundle.getString("dialog.confirm.buttons.cancel");
		boolean rtl = guiState.getLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		return DialogUtils.showConfirmationDialog(appIcon, title, headerText, contentMessage, buttonConfirmText, buttonCancelText, rtl);
	}
	
	public void showErrorDialogAndWait(String errorMessage, Exception exception)
	{
		Platform.runLater(() ->
		{
			String title = labelsBundle.getString("dialog.error.title");
			String headerText = labelsBundle.getString("dialog.error.header");
			String buttonOkText = labelsBundle.getString("dialog.error.buttons.ok");
			String moreDetailsText = labelsBundle.getString("dialog.error.buttons.showErrorDetails");
			String lessDetailsText = labelsBundle.getString("dialog.error.buttons.hideErrorDetails");
			boolean rtl = guiState.getLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			
			LOGGER.log(Level.SEVERE, errorMessage, exception);
			DialogUtils.showErrorDialog(appIcon, title, headerText, errorMessage, buttonOkText, moreDetailsText,
			                            lessDetailsText, exception, rtl);
		});
	}
	
	private void showErrorDialogAndWaitForCore(String errorCode, Exception exception, String... additionalErrorText)
	{
		String logErrorText = String.format(errorCode + ": " + errorsBundle.getString(errorCode + ".internal"), (Object[]) additionalErrorText);
		LOGGER.severe(logErrorText);
		
		String guiErrorText = String.format(errorsBundle.getString(errorCode), (Object[]) additionalErrorText);
		showErrorDialogAndWait(guiErrorText, exception);
	}
	
	/************* The following methods are used only while switching the language *************/
	
	
	private boolean showOldPage(BodyFxController bodyFxController, Map<String, Object> inputData, GuiLanguage language, StateBundle stateBundle)
	{
		BodyFxController newBodyController = showForm(bodyFxController, inputData, language);
		guiState.setBodyController(newBodyController);
		
		if(newBodyController instanceof LanguageSwitchingController) // should always be true
		{
			((LanguageSwitchingController) newBodyController).onLoadState(stateBundle);
			return true; // success
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
	
	/* used when switching the language only */
	public void switchLanguage(GuiLanguage toLanguage, LanguageSwitchingController languageSwitchingController)
	{
		LOGGER.info("Switching the GUI language to " + toLanguage.getText());
		
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
		
		URL fxmlUrl = Thread.currentThread().getContextClassLoader().getResource(FXML_FILE);
		if(fxmlUrl == null)
		{
			String errorCode = "C002-00011";
			showErrorDialogAndWaitForCore(errorCode, null);
			return;
		}
		
		String version = Context.getConfigManager().getProperty("app.version");
		String title = labelsBundle.getString("window.title") + " " + version;
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
		newStage.setOnCloseRequest(event -> LOGGER.info("The main window is closed"));
		
		StateBundle oldState = new StateBundle();
		languageSwitchingController.onSaveState(oldState);
		
		oldState.putData("stageWidth", oldStage.getWidth());
		oldState.putData("stageHeight", oldStage.getHeight());
		oldState.putData("stageX", oldStage.getX());
		oldState.putData("stageY", oldStage.getY());
		oldState.putData("stageMaximized", oldStage.isMaximized());
		
		CoreFxController newCoreFxController = newStageLoader.getController();
		newCoreFxController.guiState = guiState;
		newCoreFxController. passInitialResources(labelsBundle, errorsBundle, messagesBundle, appIcon, windowTitle, idleWarningBeforeSeconds, idleWarningAfterSeconds);
		newCoreFxController.guiState.setLanguage(toLanguage);
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
	
	/* used when switching the language only */
	private boolean applyStateBundle(StateBundle stateBundle)
	{
		BodyFxController oldBodyController = guiState.getBodyController();
		
		if(oldBodyController instanceof LanguageSwitchingController) // should always be true
		{
			Map<String, Object> inputData = oldBodyController.getInputData();
			boolean success = showOldPage(oldBodyController, inputData, guiState.getLanguage(), stateBundle);
			if(!success) return false;
		}
		else // should never happen
		{
			if(oldBodyController == null) LOGGER.severe("oldBodyController = null");
			else LOGGER.severe("oldBodyController type = " + oldBodyController.getClass().getName());
			
			String errorCode = "C002-00013";
			showErrorDialogAndWaitForCore(errorCode, null);
			return false; // no success
		}
		
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
		
		return true; // success
	}
	
	public ResourceBundle getErrorsBundle()
	{
		return errorsBundle;
	}
	
	public ResourceBundle getMessagesBundle()
	{
		return messagesBundle;
	}
}