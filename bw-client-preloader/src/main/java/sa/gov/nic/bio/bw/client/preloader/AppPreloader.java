package sa.gov.nic.bio.bw.client.preloader;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import sa.gov.nic.bio.bw.client.core.ProgressMessage;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;

public class AppPreloader extends Preloader
{
	private final Logger LOGGER = LogManager.getLogger();
	private ResourceBundle errorsBundle;
	private ResourceBundle labelsBundle;
	private Image appIcon;
	private URL fxmlUrl;
	private Stage splashScreenStage;
	private SplashScreenFxController splashScreenFxController;
	
	@Override
	public void init() throws Exception
	{
		LOGGER.info("Java FX Preloader started");
		
		CountDownLatch latch = new CountDownLatch(1); // used to wait for the dialog exit before leaving init() method
		
		try
		{
			errorsBundle = AppUtils.getResourceBundle(SplashScreenFxController.RB_ERRORS_FILE, null);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "E00-1000";
			
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
			labelsBundle = AppUtils.getResourceBundle(SplashScreenFxController.RB_LABELS_FILE, null);
		}
		catch(MissingResourceException e)
		{
			String errorCode = "E00-1001";
			
			Platform.runLater(() ->
            {
            	showErrorDialogAndWait(null, errorCode, e);
            	latch.countDown();
            });
			
			latch.await();
			return;
		}
		
		InputStream appIconStream = AppUtils.getResourceAsStream(SplashScreenFxController.APP_ICON_FILE);
		if(appIconStream == null)
		{
			String errorCode = "E00-1002";
			
			Platform.runLater(() ->
			{
				showErrorDialogAndWait(null, errorCode, null);
				latch.countDown();
			});
			
			latch.await();
			return;
		}
		appIcon = new Image(appIconStream);
		
		fxmlUrl = AppUtils.getResourceURL(SplashScreenFxController.FXML_FILE);
		if(fxmlUrl == null)
		{
			String errorCode = "E00-1003";
			
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
		LOGGER.traceEntry();
		
		FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl, labelsBundle);
		
		try
		{
			splashScreenStage = fxmlLoader.load();
		}
		catch(IOException e)
		{
			String errorCode = "E00-1004";
			showErrorDialogAndWait(appIcon, errorCode, e);
			return;
		}
		
		splashScreenFxController = fxmlLoader.getController();
		
		splashScreenStage.initStyle(StageStyle.UNDECORATED);
		splashScreenStage.show();
		
		LOGGER.info("The splash screen is shown");
		LOGGER.traceExit();
	}
	
	@Override
	public void handleProgressNotification(ProgressNotification pn)
	{
		LOGGER.debug("ProgressNotification = {}", pn.getProgress());
	}
	
	@Override
	public void handleStateChangeNotification(StateChangeNotification info)
	{
		LOGGER.debug("StateChangeNotification = {}", info.getType());
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
			
			showErrorDialogAndWait(appIcon, errorCode, exception);
		}
		else
		{
			double progress = progressMessage.getProgress();
			String message = progressMessage.getMessage();
			
			LOGGER.info("New Progress: progress({}), message({})", progress, message);
			
			splashScreenFxController.setProgress(progress);
			splashScreenFxController.setProgressMessage(message);
		}
	}
	
	private void showErrorDialogAndWait(Image appIcon, String errorCode, Exception exception)
	{
		String contentText;
		String title;
		String headerText;
		String buttonOkText;
		String moreDetailsText;
		String lessDetailsText;
		
		if(errorsBundle != null)
		{
			Message message = new ParameterizedMessage("{}: {}", errorCode, errorsBundle.getString(errorCode + ".internal"));
			LOGGER.error(message, exception);
			contentText = errorsBundle.getString(errorCode + ".ar") + " \n\n " + errorsBundle.getString(errorCode + ".en");
		}
		else // default text
		{
			LOGGER.error("\"errorsBundle\" resource bundle is missing!");
			contentText = "رمز الخطأ: E00-1000" + " \n\n " + "Error code: E00-1000";
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
		LOGGER.error("Exiting the application due to an error during the startup!");
	}
}