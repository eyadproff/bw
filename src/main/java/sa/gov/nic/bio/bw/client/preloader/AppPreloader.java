package sa.gov.nic.bio.bw.client.preloader;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.AppEntryPoint;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppInstanceManager;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.LogFormatter;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.preloader.utils.StartupErrorCodes;

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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * The app preloader that runs at the startup of the application. Its sole purpose is to perform initialization of
 * the application. A splash screen appears until the preloader finishes its job.
 *
 * @author Fouad Almalki
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
		
		// set the default language
		Preferences prefs = Preferences.userNodeForPackage(AppConstants.PREF_NODE_CLASS);
		String userLanguage = prefs.get(AppConstants.UI_LANGUAGE_PREF_NAME, null);
		if(userLanguage == null)
		{
			userLanguage = System.getProperty("user.language","en"); // Use the OS default language
		}
		
		if("ar".equalsIgnoreCase(userLanguage)) AppEntryPoint.guiLanguage = GuiLanguage.ARABIC;
		else AppEntryPoint.guiLanguage = GuiLanguage.ENGLISH;
		
		Locale.setDefault(AppEntryPoint.guiLanguage.getLocale());
	}
	
	private static final String FXML_FILE = "sa/gov/nic/bio/bw/client/preloader/fxml/gui.fxml";
	private static final String RB_STRINGS_FILE = "sa/gov/nic/bio/bw/client/preloader/bundles/strings";
	
	private static final Logger LOGGER = Logger.getLogger(AppPreloader.class.getName());
	private ResourceBundle stringsBundle;
	private URL fxmlUrl;
	private Stage splashScreenStage;
	
	@Override
	public void init() throws Exception
	{
		LOGGER.entering(AppPreloader.class.getName(), "init()");
		
		// used to wait for the dialog exit before leaving init() method.
		CountDownLatch latch = new CountDownLatch(1);
		
		try
		{
			stringsBundle = ResourceBundle.getBundle(RB_STRINGS_FILE, new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			String errorCode = StartupErrorCodes.C001_00001.getCode();
			String[] errorDetails = {"Preloader \"stringsBundle\" resource bundle is missing!"};
			
			Platform.runLater(() ->
			{
				showErrorDialogAndExit(errorCode, e, errorDetails);
				latch.countDown();
			});
			
			latch.await();
			return;
		}
		
		if(AppInstanceManager.checkIfAlreadyRunning())
		{
			Platform.runLater(() ->
			{
				String message = stringsBundle.getString("message.alreadyRunning");
			    showWarningDialogAndExit(message);
			    latch.countDown();
			});
			
			latch.await();
			return;
		}
		
		fxmlUrl = Thread.currentThread().getContextClassLoader().getResource(FXML_FILE);
		if(fxmlUrl == null)
		{
			String errorCode = StartupErrorCodes.C001_00002.getCode();
			String[] errorDetails = {"Preloader \"fxmlUrl\" is null!"};
			
			Platform.runLater(() ->
            {
            	showErrorDialogAndExit(errorCode, null, errorDetails);
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
		
		FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl, stringsBundle);
		
		try
		{
			splashScreenStage = fxmlLoader.load();
		}
		catch(IOException e)
		{
			String errorCode = StartupErrorCodes.C001_00003.getCode();
			String[] errorDetails = {"Failed to load the splash screen FXML correctly!"};
			showErrorDialogAndExit(errorCode, e, errorDetails);
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
		sa.gov.nic.bio.bw.client.core.utils.PreloaderNotification preloaderNotification =
												(sa.gov.nic.bio.bw.client.core.utils.PreloaderNotification) info;
		
		if(preloaderNotification.isDone())
		{
			splashScreenStage.hide();
			
			if(preloaderNotification.isSuccess())
			{
				LOGGER.info("The splash screen is closed");
			}
			else
			{
				String errorCode = preloaderNotification.getErrorCode();
				Exception exception = preloaderNotification.getException();
				String[] errorDetails = preloaderNotification.getErrorDetails();
				
				showErrorDialogAndExit(errorCode, exception, errorDetails);
			}
		}
	}
	
	/**
	 * Show a warning message in a dialog and then exit.
	 *
	 * @param message the warning message to show
	 */
	private void showWarningDialogAndExit(String message)
	{
		String title;
		String buttonExitText;
		
		if(stringsBundle != null)
		{
			title = stringsBundle.getString("dialog.warning.title");
			buttonExitText = stringsBundle.getString("dialog.warning.buttons.exit");
		}
		else
		{
			title = "Warning Message";
			buttonExitText = "Exit";
		}
		
		LOGGER.warning(message);
		DialogUtils.showAlertDialog(AlertType.WARNING, null, null, title,
		                            message, null, null, buttonExitText,
		                           null, null,
		                           AppEntryPoint.guiLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT);
		
		LOGGER.severe("Exiting the application due to an error during the startup!");
		Platform.exit();
	}
	
	/**
	 * Show details about the error that occurs during the startup in a dialog and then exit.
	 *
	 * @param errorCode the error code
	 * @param exception the exception, if any
	 * @param errorDetails details about the error
	 */
	private void showErrorDialogAndExit(String errorCode, Exception exception, String[] errorDetails)
	{
		String title;
		String headerText;
		String contentText;
		String buttonExitText;
		String moreDetailsText;
		String lessDetailsText;
		String errorOccursText;
		
		if(stringsBundle != null)
		{
			title = stringsBundle.getString("dialog.error.title");
			headerText = stringsBundle.getString("dialog.error.header");
			buttonExitText = stringsBundle.getString("dialog.error.buttons.exit");
			moreDetailsText = stringsBundle.getString("dialog.error.buttons.showErrorDetails");
			lessDetailsText = stringsBundle.getString("dialog.error.buttons.hideErrorDetails");
			errorOccursText = stringsBundle.getString("message.errorOccurs");
		}
		else
		{
			title = "Error Message";
			headerText = "An error occurs during application startup!";
			buttonExitText = "Exit";
			moreDetailsText = "Show error details";
			lessDetailsText = "Hide error details";
			errorOccursText = "An error occurs. Error code: %s";
		}
		
		StringBuilder sb = new StringBuilder();
		contentText = String.format(errorOccursText, errorCode);
		GuiUtils.buildErrorMessage(exception, errorDetails, sb);
		
		LOGGER.log(Level.SEVERE, contentText + (sb.length() > 0 ? "\n" : "") + sb.toString(), exception);
		DialogUtils.showAlertDialog(AlertType.ERROR, null, null, title, headerText,
		                            contentText, sb.toString(), buttonExitText, moreDetailsText, lessDetailsText,
		                           AppEntryPoint.guiLanguage.getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT);
		
		LOGGER.severe("Exiting the application due to an error during the startup!");
		Platform.exit();
	}
}