package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sa.gov.nic.bio.bw.client.core.beans.BusinessData;
import sa.gov.nic.bio.bw.client.core.beans.GuiState;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.beans.UserData;
import sa.gov.nic.bio.bw.client.core.interfaces.*;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class CoreFxController
{
	private static final Logger LOGGER = Logger.getLogger(CoreFxController.class.getName());
	public static final String FXML_FILE = "sa/gov/nic/bio/bw/client/core/fxml/core.fxml";
	public static final String APP_ICON_FILE = "sa/gov/nic/bio/bw/client/core/images/app_icon.png";
	public static final String RB_LABELS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/labels";
	public static final String RB_ERRORS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/errors";
	public static final String RB_MESSAGES_FILE = "sa/gov/nic/bio/bw/client/core/bundles/messages";
	private static final String KEY_STAGE_WIDTH = "stageWidth";
	private static final String KEY_STAGE_HEIGHT = "stageHeight";
	private static final String KEY_STAGE_X = "stageX";
	private static final String KEY_STAGE_Y = "stageY";
	private static final String KEY_STAGE_MAXIMIZED = "stageMaximized";
	
	@FXML private ResourceBundle resources;
	@FXML private Stage primaryStage;
	@FXML private HeaderPaneFxController headerPaneController;
	@FXML private FooterPaneFxController footerPaneController;
	@FXML private MenuPaneFxController menuPaneController;
	@FXML private StackPane bodyPane;
	
	private ResourceBundle errorsBundle;
	private ResourceBundle messagesBundle;
	private Image appIcon;
	
	private GuiState guiState = new GuiState();
	private UserData userData = new UserData();
	private BusinessData businessData = new BusinessData(); // TODO: fill it at startup?
	
	private Executor executor = Executors.newWorkStealingPool();
	//private Executor executor = Executors.newSingleThreadExecutor();
	
	public void passInitialResources(ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon)
	{
		this.errorsBundle = errorsBundle;
		this.messagesBundle = messagesBundle;
		this.appIcon = appIcon;
	}
	
	public GuiState getGuiState(){return guiState;}
	public UserData getUserData(){return userData;}
	public BusinessData getBusinessData(){return businessData;}
	
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
		
		if(guiState.getBodyController() == null) // if this is the first load
		{
			executor.execute(() -> Context.getWorkflowManager().startProcess(this::showPage));
		}
	}
	
	public void submitFormTask(String taskId, Map<String, String> uiDataMap)
	{
		executor.execute(() -> Context.getWorkflowManager().submitFormTask(taskId, uiDataMap, this::showPage));
	}
	
	private void showPage(String formKey, String taskId, Map<String, Object> inputData)
	{
		Boolean keepSameForm = (Boolean) inputData.get("keepSameForm");
		
		if(keepSameForm != null && keepSameForm)
		{
			guiState.getBodyController().onReturnFromTask(taskId, inputData);
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
				// TODO: report for error
				e.printStackTrace();
				return;
			}
			
			Platform.runLater(() -> showPage(bodyFxController, taskId, inputData, guiState.getLanguage()));
		}
	}
	
	private BodyFxController showPage(BodyFxController bodyFxController, String taskId, Map<String, Object> inputData, GuiLanguage language)
	{
		URL fxmlUrl = bodyFxController.getFxmlLocation();
		if(fxmlUrl == null)
		{
			String errorCode = "E01-1001";
			showErrorDialogAndWait(appIcon, errorCode, null, bodyFxController.getClass().getName());
			return null;
		}
		
		ResourceBundleCollection resourceBundleCollection = bodyFxController.getResourceBundleCollection();
		
		ResourceBundle labelsBundle;
		try
		{
			labelsBundle = AppUtils.getResourceBundle(resourceBundleCollection.getLabelsBundlePath(), language);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "E01-1002";
			showErrorDialogAndWait(appIcon, errorCode, e, bodyFxController.getClass().getName());
			return null;
		}
		
		ResourceBundle errorsBundle;
		try
		{
			errorsBundle = AppUtils.getResourceBundle(resourceBundleCollection.getErrorsBundlePath(), language);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "E01-1003";
			showErrorDialogAndWait(appIcon, errorCode, e, bodyFxController.getClass().getName());
			return null;
		}
		
		ResourceBundle messagesBundle;
		try
		{
			messagesBundle = AppUtils.getResourceBundle(resourceBundleCollection.getMessagesBundlePath(), language);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "E01-1004";
			showErrorDialogAndWait(appIcon, errorCode, e, bodyFxController.getClass().getName());
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
			String errorCode = "E01-1005";
			showErrorDialogAndWait(appIcon, errorCode, e, bodyFxController.getClass().getName());
			return null;
		}
		
		BodyFxController controller = paneLoader.getController();
		guiState.setBodyController(controller);
		
		controller.attachCoreFxController(this);
		controller.attachInitialResources(errorsBundle, messagesBundle, appIcon);
		controller.attachTaskId(taskId);
		controller.attachInputData(inputData);
		
		bodyPane.getChildren().setAll(loadedPane);
		controller.onControllerReady();
		
		return controller;
	}
	
	private void showErrorDialogAndWait(Image appIcon, String errorCode, Exception exception, String... additionalErrorText)
	{
		String contentText;
		String title;
		String headerText;
		String buttonOkText;
		String moreDetailsText;
		String lessDetailsText;
		
		String errorText = String.format(errorCode + ": " + errorsBundle.getString(errorCode + ".internal"), (Object[]) additionalErrorText);
		if(additionalErrorText != null) LOGGER.severe(errorText);
		else LOGGER.severe(errorCode + ": " + errorsBundle.getString(errorCode + ".internal"));
		contentText = errorsBundle.getString(errorCode);
		title = resources.getString("dialog.error.title");
		headerText = resources.getString("dialog.error.header");
		buttonOkText = resources.getString("dialog.error.buttons.ok");
		moreDetailsText = resources.getString("dialog.error.buttons.showErrorDetails");
		lessDetailsText = resources.getString("dialog.error.buttons.hideErrorDetails");
		
		DialogUtils.showErrorDialog(appIcon, title, headerText, contentText, buttonOkText, moreDetailsText,
		                            lessDetailsText, exception);
	}
	
	/************* The following methods are used only while switching the language *************/
	
	
	private boolean showOldPage(BodyFxController bodyFxController, String taskId, Map<String, Object> inputData, GuiLanguage language, StateBundle stateBundle)
	{
		BodyFxController newBodyController = showPage(bodyFxController, taskId, inputData, language);
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
			
			String errorCode = "E01-1006";
			showErrorDialogAndWait(appIcon, errorCode, null);
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
			labelsBundle = AppUtils.getResourceBundle(RB_LABELS_FILE, toLanguage);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "E01-1007";
			showErrorDialogAndWait(appIcon, errorCode, e);
			return;
		}
		
		ResourceBundle errorsBundle;
		try
		{
			errorsBundle = AppUtils.getResourceBundle(RB_ERRORS_FILE, toLanguage);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "E01-1008";
			showErrorDialogAndWait(appIcon, errorCode, e);
			return;
		}
		
		ResourceBundle messagesBundle;
		try
		{
			messagesBundle = AppUtils.getResourceBundle(RB_MESSAGES_FILE, toLanguage);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "E01-1009";
			showErrorDialogAndWait(appIcon, errorCode, e);
			return;
		}
		
		URL fxmlUrl = AppUtils.getResourceURL(FXML_FILE);
		if(fxmlUrl == null)
		{
			String errorCode = "E01-1010";
			showErrorDialogAndWait(appIcon, errorCode, null);
			return;
		}
		
		FXMLLoader newStageLoader = new FXMLLoader(fxmlUrl, labelsBundle);
		
		Stage oldStage = primaryStage;
		
		Stage newStage;
		try
		{
			newStage = newStageLoader.load();
		}
		catch(IOException e)
		{
			String errorCode = "E01-1011";
			showErrorDialogAndWait(appIcon, errorCode, e);
			return;
		}
		
		newStage.getScene().setNodeOrientation(toLanguage.getNodeOrientation());
		newStage.setOnCloseRequest(event -> LOGGER.info("The main window is closed"));
		
		StateBundle oldState = new StateBundle();
		languageSwitchingController.onSaveState(oldState);
		
		oldState.putData(KEY_STAGE_WIDTH, oldStage.getWidth());
		oldState.putData(KEY_STAGE_HEIGHT, oldStage.getHeight());
		oldState.putData(KEY_STAGE_X, oldStage.getX());
		oldState.putData(KEY_STAGE_Y, oldStage.getY());
		oldState.putData(KEY_STAGE_MAXIMIZED, oldStage.isMaximized());
		
		CoreFxController newCoreFxController = newStageLoader.getController();
		newCoreFxController.guiState = guiState;
		newCoreFxController.passInitialResources(errorsBundle, messagesBundle, appIcon);
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
			String taskId = oldBodyController.getTaskId();
			Map<String, Object> inputData = oldBodyController.getInputData();
			boolean success = showOldPage(oldBodyController, taskId, inputData, guiState.getLanguage(), stateBundle);
			if(!success) return false;
		}
		else // should never happen
		{
			if(oldBodyController == null) LOGGER.severe("oldBodyController = null");
			else LOGGER.severe("oldBodyController type = " + oldBodyController.getClass().getName());
			
			String errorCode = "E01-1012";
			showErrorDialogAndWait(appIcon, errorCode, null);
			return false; // no success
		}
		
		double width = stateBundle.getDate(KEY_STAGE_WIDTH, Double.class);
		double height = stateBundle.getDate(KEY_STAGE_HEIGHT, Double.class);
		double x = stateBundle.getDate(KEY_STAGE_X, Double.class);
		double y = stateBundle.getDate(KEY_STAGE_Y, Double.class);
		boolean maximized = stateBundle.getDate(KEY_STAGE_MAXIMIZED, Boolean.class);
		
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
}