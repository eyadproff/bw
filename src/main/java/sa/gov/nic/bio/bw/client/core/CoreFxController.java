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
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class CoreFxController
{
	private static final Logger LOGGER = Logger.getLogger(CoreFxController.class.getName());
	public static final String FXML_FILE = "sa/gov/nic/bio/bw/client/core/fxml/core.fxml";
	public static final String APP_ICON_FILE = "sa/gov/nic/bio/bw/client/core/images/app_icon.png";
	public static final String RB_LABELS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/labels";
	public static final String RB_ERRORS_FILE = "sa/gov/nic/bio/bw/client/core/bundles/errors";
	public static final String RB_MESSAGES_FILE = "sa/gov/nic/bio/bw/client/core/bundles/messages";
	
	@FXML private ResourceBundle resources;
	@FXML private Stage primaryStage;
	@FXML private HeaderPaneFxController headerPaneController;
	@FXML private FooterPaneFxController footerPaneController;
	@FXML private MenuPaneFxController menuPaneController;
	@FXML private StackPane bodyPane;
	
	private ResourceBundle labelsBundle;
	private ResourceBundle errorsBundle;
	private ResourceBundle messagesBundle;
	private Image appIcon;
	private String windowTitle;
	
	private GuiState guiState = new GuiState();
	private UserData userData = new UserData();
	private BusinessData businessData = new BusinessData(); // TODO: fill it at startup?
	
	public void passInitialResources(ResourceBundle labelsBundle, ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon, String windowTitle)
	{
		this.labelsBundle = labelsBundle;
		this.errorsBundle = errorsBundle;
		this.messagesBundle = messagesBundle;
		this.appIcon = appIcon;
		this.windowTitle = windowTitle;
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
			Runnable runnable = () -> Context.getWorkflowManager().startProcess(this::showForm);
			Context.getExecutorService().execute(runnable);
		}
		
		primaryStage.setTitle(windowTitle);
	}
	
	public void submitFormTask(Map<String, String> uiDataMap)
	{
		Runnable runnable = () -> Context.getWorkflowManager().submitFormTask(uiDataMap, this::showForm);
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
			labelsBundle = AppUtils.getResourceBundle(resourceBundleCollection.getLabelsBundlePath(), language);
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
			errorsBundle = AppUtils.getResourceBundle(resourceBundleCollection.getErrorsBundlePath(), language);
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
			messagesBundle = AppUtils.getResourceBundle(resourceBundleCollection.getMessagesBundlePath(), language);
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
		controller.attachInitialResources(errorsBundle, messagesBundle, appIcon);
		controller.attachInputData(inputData);
		
		bodyPane.getChildren().setAll(loadedPane);
		controller.onControllerReady();
		
		return controller;
	}
	
	public void showErrorDialogAndWait(String errorMessage, Exception exception)
	{
		Platform.runLater(() ->
		{
			String title;
			String headerText;
			String buttonOkText;
			String moreDetailsText;
			String lessDetailsText;
			
			
			title = resources.getString("dialog.error.title");
			headerText = resources.getString("dialog.error.header");
			buttonOkText = resources.getString("dialog.error.buttons.ok");
			moreDetailsText = resources.getString("dialog.error.buttons.showErrorDetails");
			lessDetailsText = resources.getString("dialog.error.buttons.hideErrorDetails");
			
			DialogUtils.showErrorDialog(appIcon, title, headerText, errorMessage, buttonOkText, moreDetailsText,
			                            lessDetailsText, exception);
		});
	}
	
	public void showErrorDialogAndWaitForCore(String errorCode, Exception exception, String... additionalErrorText)
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
			labelsBundle = AppUtils.getResourceBundle(RB_LABELS_FILE, toLanguage);
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
			errorsBundle = AppUtils.getResourceBundle(RB_ERRORS_FILE, toLanguage);
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
			messagesBundle = AppUtils.getResourceBundle(RB_MESSAGES_FILE, toLanguage);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C002-00010";
			showErrorDialogAndWaitForCore(errorCode, e);
			return;
		}
		
		URL fxmlUrl = AppUtils.getResourceURL(FXML_FILE);
		if(fxmlUrl == null)
		{
			String errorCode = "C002-00011";
			showErrorDialogAndWaitForCore(errorCode, null);
			return;
		}
		
		String version = Context.getConfigManager().getProperty("app.version");
		String title = labelsBundle.getString("window.title") + " " + version;
		windowTitle = AppUtils.replaceNumbers(title, Locale.getDefault());
		
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
		newCoreFxController. passInitialResources(labelsBundle, errorsBundle, messagesBundle, appIcon, windowTitle);
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
}