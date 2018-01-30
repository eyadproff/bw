package sa.gov.nic.bio.bw.client.preloader;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.LogFormatter;
import sa.gov.nic.bio.bw.client.core.utils.ProgressMessage;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The app preloader that runs at the startup of the application. Its sole purpose is to perform initialization of
 * the application. A splash screen appears until the preloader finishes its job.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public class AppPreloader extends Preloader
{
	static
	{
		// Delete the deployment file that is created by launching javaws. This is a workaround for the buggy
		// webstart system. The users' machines have JRE6 installed on them. Whenever we run JRE8's javaws on
		// a machine that has JRE6, a new record of JRE8 is added to the deployment file. Now, if we try to
		// run any JNLP with JRE6's javaws, it will not run! The workaround solution to this bug is to delete
		// the deployment file after launching by JRE8's javaws. The next time JRE6's javaws runs, it will not
		// find the deployment file, it will create it as on new machines and run successfully.
		
		// NOTE: we needed this workaround when we was launching BW via as a webstart via JRE8's javaws.
		// However, it is no longer the case now. BW is currently launched by BCL which in turn is launched
		// by JRE6's javaws. I chose to keep this workaround just in case someone by mistake invokes JRE8's
		// javaws that is downloaded by the BCL on a separate folder (not installed).
		
		String deploymentFilePath = System.getProperty("user.home") +
								"/AppData/LocalLow/Sun/Java/Deployment/deployment.properties"; // Windows 7 and newer
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
		
		// check if LOGS_FOLDER_PATH exists. If not, create it.
		Path logFolderPath = Paths.get(AppConstants.LOGS_FOLDER_PATH);
		if(!Files.exists(logFolderPath))
		{
			try
			{
				Files.createDirectories(logFolderPath);
				System.out.println("Created the log folder (" + AppConstants.LOGS_FOLDER_PATH + ").");
			}
			catch(IOException e)
			{
				System.out.println("Failed to create the log folder (" + AppConstants.LOGS_FOLDER_PATH + ")!");
				e.printStackTrace();
			}
		}
		
		// check if TEMP_FOLDER_PATH exists. If not, create it.
		Path tempFolderPath = Paths.get(AppConstants.TEMP_FOLDER_PATH);
		if(!Files.exists(tempFolderPath))
		{
			try
			{
				Files.createDirectories(tempFolderPath);
				System.out.println("Created the temp folder (" + AppConstants.TEMP_FOLDER_PATH + ").");
			}
			catch(IOException e)
			{
				System.out.println("Failed to create the temp folder (" + AppConstants.TEMP_FOLDER_PATH + ")!");
				e.printStackTrace();
			}
		}
		
		// clean the temp folder.
		try
		{
			AppUtils.cleanDirectory(tempFolderPath);
		}
		catch(IOException e)
		{
			System.out.println("Failed to clean the temp folder (" + AppConstants.TEMP_FOLDER_PATH + ")!");
			e.printStackTrace();
		}
		
		// initialize the logging.
		try
		{
			InputStream inputStream = AppPreloader.class.getResourceAsStream(
													"/sa/gov/nic/bio/bw/client/core/config/logging.properties");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			
			String line;
			while((line = br.readLine()) != null)
			{
				sb.append(line);
				sb.append("\n");
			}
			br.close();
			
			String content = sb.toString();
			content = content.replace("${user.name}", System.getProperty("user.name"));
			
			InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
			LogManager.getLogManager().readConfiguration(is);
			Handler[] handlers = Logger.getGlobal().getParent().getHandlers();
			Formatter formatter = new LogFormatter();
			for(Handler h : handlers) h.setFormatter(formatter);
		}
		catch(Exception e)
		{
			Logger.getAnonymousLogger().log(Level.SEVERE, "Could not load logging.properties file", e);
		}
	}
	
	private static final String FXML_FILE = "sa/gov/nic/bio/bw/client/preloader/fxml/gui.fxml";
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
		LOGGER.entering(AppPreloader.class.getName(), "init()");
		CountDownLatch latch = new CountDownLatch(1); // used to wait for the dialog exit before leaving init() method.
		
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
		
		LOGGER.exiting(AppPreloader.class.getName(), "init()");
	}
	
	@Override
	public void start(Stage ignoredStage)
	{
		LOGGER.entering(AppPreloader.class.getName(), "start(Stage ignoredStage)");
		
		// start() is called from the JavaFX UI thread. Creation of JavaFX Scene and Stage objects as well as
		// modification of scene graph operations to live objects (those objects already attached to a scene)
		// must be done on the JavaFX UI thread.
		
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
		
		splashScreenStage.initStyle(StageStyle.UNDECORATED); // remove the default system window's borders.
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
	
	/**
	 * Show details about the error that occurs during the startup in a dialog.
	 *
	 * @param appIcon icon that is used in the dialog
	 * @param errorCode the error code
	 * @param exception the exception, if any
	 * @param additionalErrorText any extra parameters to the error message that is loaded by <code>errorCode</code>
	 */
	private void showErrorDialogAndWait(Image appIcon, String errorCode, Exception exception,
	                                    String... additionalErrorText)
	{
		String contentText;
		String title;
		String headerText;
		String buttonOkText;
		String moreDetailsText;
		String lessDetailsText;
		
		if(errorCode != null && errorCode.startsWith("C002")) // most likely C002 error during calling
															  // webservices API during startup.
		{
			String message = String.format(errorsBundle.getString("C000-00000.internal"), errorCode) + " - " +
							 Arrays.toString(additionalErrorText);
			
			LOGGER.log(Level.SEVERE, message, exception);
			
			contentText = String.format(errorsBundle.getString("C000-00000.ar"), errorCode) + " \n\n" +
						  String.format(errorsBundle.getString("C000-00000.en"), errorCode) + "\n\n" +
						  Arrays.toString(additionalErrorText);
		}
		else if(errorsBundle != null)
		{
			String message = errorCode + ": " + errorsBundle.getString(errorCode + ".internal");
			if(additionalErrorText != null) message = String.format(message, (Object[]) additionalErrorText);
			LOGGER.log(Level.SEVERE, message, exception);
			contentText = errorsBundle.getString(errorCode + ".ar");
			if(additionalErrorText != null) contentText = String.format(contentText, (Object[]) additionalErrorText);
			contentText += " \n\n ";
			String temp = errorsBundle.getString(errorCode + ".en");
			if(additionalErrorText != null) temp = String.format(temp, (Object[]) additionalErrorText);
			contentText += temp;
		}
		else // default text
		{
			LOGGER.severe("\"errorsBundle\" resource bundle is missing!");
			contentText = "رمز الخطأ: C001-00001" + " \n\n " + "Error code: C001-00001";
		}
		
		if(labelsBundle != null)
		{
			title = labelsBundle.getString("dialog.error.title.ar") + " - " +
					labelsBundle.getString("dialog.error.title.en");
			
			headerText = labelsBundle.getString("dialog.error.header.ar") + " \n\n " +
						 labelsBundle.getString("dialog.error.header.en");
			
			buttonOkText = labelsBundle.getString("dialog.error.buttons.ok.ar") + " - " +
						   labelsBundle.getString("dialog.error.buttons.ok.en");
			
			moreDetailsText = labelsBundle.getString("dialog.error.buttons.showErrorDetails.ar") + " - " +
							  labelsBundle.getString("dialog.error.buttons.showErrorDetails.en");
			
			lessDetailsText = labelsBundle.getString("dialog.error.buttons.hideErrorDetails.ar") + " - " +
							  labelsBundle.getString("dialog.error.buttons.hideErrorDetails.en");
		}
		else // default text
		{
			title = "رسالة خطأ" + " - " + "Error Message";
			headerText = "حدث خطأ أثناء تشغيل البرنامج!" + " \n\n " + "!An error occurs during application startup";
			buttonOkText = "حسناً" + " - " + "OK";
			moreDetailsText = "إظهار تفاصيل الخطأ" + " - " + "Show error details";
			lessDetailsText = "إخفاء تفاصيل الخطأ" + " - " + "Hide error details";
		}
		
		DialogUtils.showErrorDialog(null, null, appIcon, title, headerText,
		                            contentText, buttonOkText, moreDetailsText, lessDetailsText, exception, true);
		
		Platform.exit();
		LOGGER.severe("Exiting the application due to an error during the startup!");
	}
}