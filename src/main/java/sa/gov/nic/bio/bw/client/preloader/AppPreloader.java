package sa.gov.nic.bio.bw.client.preloader;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.ProgressMessage;


public class AppPreloader extends Preloader
{
	static
	{
		Locale.setDefault(GuiLanguage.ARABIC.getLocale());
		
		InputStream inputStream = AppUtils.getResourceAsStream("sa/gov/nic/bio/bw/client/core/config/logging.properties");
		try
		{
			LogManager.getLogManager().readConfiguration(inputStream);
		}
		catch(IOException e)
		{
			Logger.getAnonymousLogger().severe("Could not load logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}
	
	private static final String FXML_FILE = "sa/gov/nic/bio/bw/client/preloader/fxml/splash_screen.fxml";
	private static final String APP_ICON_FILE = "sa/gov/nic/bio/bw/client/preloader/images/app_icon.png";
	private static final String RB_LABELS_FILE = "sa/gov/nic/bio/bw/client/preloader/bundles/labels";
	private static final String RB_ERRORS_FILE = "sa/gov/nic/bio/bw/client/preloader/bundles/errors";
	
	private static final Logger LOGGER = Logger.getLogger(AppPreloader.class.getName());
	private ResourceBundle errorsBundle;
	private ResourceBundle labelsBundle;
	private Image appIcon;
	private URL fxmlUrl;
	private Stage splashScreenStage;
	
	@Override
	public void init() throws Exception
	{
		CountDownLatch latch = new CountDownLatch(1); // used to wait for the dialog exit before leaving init() method
		
		try
		{
			errorsBundle = AppUtils.getResourceBundle(RB_ERRORS_FILE, null);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C001-00001";
			
			Platform.runLater(() ->
			{
				showErrorDialogAndWait(null, errorCode, e);
				latch.countDown();
			});
			
			latch.await();
			return;
		}
		
		try
		{
			labelsBundle = AppUtils.getResourceBundle(RB_LABELS_FILE, null);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "C001-00002";
			
			Platform.runLater(() ->
			{
				showErrorDialogAndWait(null, errorCode, e);
				latch.countDown();
			});
			
			latch.await();
			return;
		}
		
		InputStream appIconStream = AppUtils.getResourceAsStream(APP_ICON_FILE);
		if(appIconStream == null)
		{
			String errorCode = "C001-00003";
			
			Platform.runLater(() ->
			{
				showErrorDialogAndWait(null, errorCode, null);
				latch.countDown();
			});
			
			latch.await();
			return;
		}
		appIcon = new Image(appIconStream);
		
		fxmlUrl = AppUtils.getResourceURL(FXML_FILE);
		if(fxmlUrl == null)
		{
			String errorCode = "C001-00004";
			
			Platform.runLater(() ->
            {
                showErrorDialogAndWait(appIcon, errorCode, null);
                latch.countDown();
            });
			
			latch.await();
		}
	}
	
	@Override
	public void start(Stage ignoredStage)
	{
		LOGGER.entering(AppPreloader.class.getName(), "start(Stage ignoredStage)");
		
		FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl, labelsBundle);
		
		try
		{
			splashScreenStage = fxmlLoader.load();
		}
		catch(IOException e)
		{
			String errorCode = "C001-00005";
			showErrorDialogAndWait(appIcon, errorCode, e);
			return;
		}
		
		splashScreenStage.initStyle(StageStyle.UNDECORATED);
		splashScreenStage.show();
		
		LOGGER.info("The splash screen is shown");
		LOGGER.exiting(AppPreloader.class.getName(), "start(Stage ignoredStage)");
	}
	
	@Override
	public void handleProgressNotification(ProgressNotification pn)
	{
		LOGGER.fine("ProgressNotification = " + pn.getProgress());
	}
	
	@Override
	public void handleStateChangeNotification(StateChangeNotification info)
	{
		LOGGER.fine("StateChangeNotification = " + info.getType());
	}
	
	@Override
	public void handleApplicationNotification(PreloaderNotification info)
	{
		ProgressMessage progressMessage = (ProgressMessage) info;
		
		if(progressMessage.isDone())
		{
			splashScreenStage.hide();
			LOGGER.info("The splash screen is closed");
		}
		else if(progressMessage.isFailed())
		{
			splashScreenStage.hide();
			
			String errorCode = progressMessage.getErrorCode();
			Exception exception = progressMessage.getException();
			Object[] errorMessageValues = progressMessage.getAdditionalErrorText();
			
			showErrorDialogAndWait(appIcon, errorCode, exception, errorMessageValues);
		}
	}
	
	private void showErrorDialogAndWait(Image appIcon, String errorCode, Exception exception, Object... errorMessageValues)
	{
		String contentText;
		String title;
		String headerText;
		String buttonOkText;
		String moreDetailsText;
		String lessDetailsText;
		
		if(errorsBundle != null)
		{
			String message = errorCode + ": " + errorsBundle.getString(errorCode + ".internal");
			LOGGER.log(Level.SEVERE, message, exception);
			contentText = errorsBundle.getString(errorCode + ".ar") + " \n\n " + errorsBundle.getString(errorCode + ".en");
			if(errorMessageValues != null) contentText = String.format(contentText, errorMessageValues);
		}
		else // default text
		{
			LOGGER.severe("\"errorsBundle\" resource bundle is missing!");
			contentText = "رمز الخطأ: C001-00001" + " \n\n " + "Error code: C001-00001";
		}
		
		if(labelsBundle != null)
		{
			title = labelsBundle.getString("dialog.error.title.ar") + " - " + labelsBundle.getString("dialog.error.title.en");
			headerText = labelsBundle.getString("dialog.error.header.ar") + " \n\n " + labelsBundle.getString("dialog.error.header.en");
			buttonOkText = labelsBundle.getString("dialog.error.buttons.ok.ar") + " - " + labelsBundle.getString("dialog.error.buttons.ok.en");
			moreDetailsText = labelsBundle.getString("dialog.error.buttons.showErrorDetails.ar") + " - " + labelsBundle.getString("dialog.error.buttons.showErrorDetails.en");
			lessDetailsText = labelsBundle.getString("dialog.error.buttons.hideErrorDetails.ar") + " - " + labelsBundle.getString("dialog.error.buttons.hideErrorDetails.en");
		}
		else // default text
		{
			title = "رسالة خطأ" + " - " + "Error Message";
			headerText = "حدث خطأ أثناء تشغيل البرنامج!" + " \n\n " + "!An error occurs during application startup";
			buttonOkText = "حسناً" + " - " + "OK";
			moreDetailsText = "إظهار تفاصيل الخطأ" + " - " + "Show error details";
			lessDetailsText = "إخفاء تفاصيل الخطأ" + " - " + "Hide error details";
		}
		
		DialogUtils.showErrorDialog(appIcon, title, headerText, contentText, buttonOkText, moreDetailsText, lessDetailsText, exception);
		
		Platform.exit();
		LOGGER.severe("Exiting the application due to an error during the startup!");
	}
}