package sa.gov.nic.bio.bw.client.preloader;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.*;
import java.util.logging.Formatter;

import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.core.utils.*;


public class AppPreloader extends Preloader
{
	static
	{
		String deploymentFilePath = System.getProperty("user.home") + "/AppData/LocalLow/Sun/Java/Deployment/deployment.properties";
		try
		{
			Files.deleteIfExists(Paths.get(deploymentFilePath));
			System.out.println("Deleted the user-level deployment.properties.");
		}
		catch(IOException e)
		{
			System.out.println("Failed to delete the user-level deployment.properties!");
			e.printStackTrace();
		}
		
		Locale.setDefault(GuiLanguage.ARABIC.getLocale());
		
		// check if c:/bio/logs/bw exists. If not, create it
		Path logFolderPath = Paths.get("c:/bio/logs/bw");
		if(!Files.exists(logFolderPath))
		{
			try
			{
				Files.createDirectories(logFolderPath);
				System.out.println("Created the log folder (C:\\bio\\logs\\bw).");
			}
			catch(IOException e)
			{
				System.out.println("Failed to create the log folder (C:\\bio\\logs\\bw)!");
				e.printStackTrace();
			}
		}
		
		InputStream inputStream = AppPreloader.class.getResourceAsStream("/sa/gov/nic/bio/bw/client/core/config/logging.properties");
		try
		{
			LogManager.getLogManager().readConfiguration(inputStream);
			Handler[] handlers = Logger.getGlobal().getParent().getHandlers();
			Formatter formatter = new LogFormatter();
			for(Handler h : handlers) h.setFormatter(formatter);
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
			errorsBundle = ResourceBundle.getBundle(RB_ERRORS_FILE, new UTF8Control());
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
			labelsBundle = ResourceBundle.getBundle(RB_LABELS_FILE, new UTF8Control());
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
		
		InputStream appIconStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_ICON_FILE);
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
		
		fxmlUrl = Thread.currentThread().getContextClassLoader().getResource(FXML_FILE);
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
			String[] errorMessageValues = progressMessage.getAdditionalErrorText();
			
			showErrorDialogAndWait(appIcon, errorCode, exception, errorMessageValues);
		}
	}
	
	private void showErrorDialogAndWait(Image appIcon, String errorCode, Exception exception, String... additionalErrorText)
	{
		String contentText;
		String title;
		String headerText;
		String buttonOkText;
		String moreDetailsText;
		String lessDetailsText;
		
		if(errorCode != null && errorCode.startsWith("C002")) // most likely C002 error during calling webservices API during startup. TODO: change this later
		{
			String message = String.format(errorsBundle.getString("C000-00000.internal"), errorCode);
			LOGGER.log(Level.SEVERE, message, exception);
			contentText = String.format(errorsBundle.getString("C000-00000.ar"), errorCode) + " \n\n " + String.format(errorsBundle.getString("C000-00000.en"), errorCode);
		}
		else if(errorsBundle != null)
		{
			String message = errorCode + ": " + errorsBundle.getString(errorCode + ".internal");
			if(additionalErrorText != null) message = String.format(message, (Object[]) additionalErrorText);
			LOGGER.log(Level.SEVERE, message, exception);
			contentText = errorsBundle.getString(errorCode + ".ar") + " \n\n " + errorsBundle.getString(errorCode + ".en");
			if(additionalErrorText != null) contentText = String.format(contentText, (Object[]) additionalErrorText);
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
		
		DialogUtils.showErrorDialog(appIcon, title, headerText, contentText, buttonOkText, moreDetailsText, lessDetailsText, exception, true);
		
		Platform.exit();
		LOGGER.severe("Exiting the application due to an error during the startup!");
	}
}