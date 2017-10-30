package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.bw.client.core.interfaces.BodyFxController;
import sa.gov.nic.bio.bw.client.core.interfaces.ResourceBundleCollection;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public abstract class BodyFxControllerBase implements BodyFxController
{
	private static final Logger LOGGER = Logger.getLogger(BodyFxControllerBase.class.getName());
	protected CoreFxController coreFxController;
	protected ResourceBundle labelsBundle;
	protected ResourceBundle errorsBundle;
	protected ResourceBundle messagesBundle;
	protected Image appIcon;
	protected Map<String, Object> inputData;
	
	@FXML protected NotificationPane notificationPane;
	
	private Image successIcon = new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/success.png"));
	private Image warningIcon = new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/warning.png"));
	private Image errorIcon = new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/error.png"));
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/gui.fxml");
	}
	
	@Override
	public ResourceBundleCollection getResourceBundleCollection()
	{
		String base = getClass().getPackage().getName().replace(".", "/");
		
		return new ResourceBundleCollection()
		{
			@Override
			public String getLabelsBundlePath()
			{
				return base + "/bundles/labels";
			}
			
			@Override
			public String getErrorsBundlePath()
			{
				return base + "/bundles/errors";
			}
			
			@Override
			public String getMessagesBundlePath()
			{
				return base + "/bundles/messages";
			}
		};
	}
	
	@Override
	public void attachCoreFxController(CoreFxController coreFxController)
	{
		this.coreFxController = coreFxController;
	}
	
	@Override
	public void attachInitialResources(ResourceBundle labelsBundle, ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon)
	{
		this.labelsBundle = labelsBundle;
		this.errorsBundle = errorsBundle;
		this.messagesBundle = messagesBundle;
		this.appIcon = appIcon;
	}
	
	@Override
	public void attachInputData(Map<String, Object> inputData)
	{
		this.inputData = inputData;
	}
	
	@Override
	public Map<String, Object> getInputData()
	{
		return inputData;
	}
	
	@Override
	public void onReturnFromTask(Map<String, Object> inputData)
	{
		this.inputData = inputData;
		Platform.runLater(this::onReturnFromTask);
	}
	
	public void onReturnFromTask()
	{
		Boolean successResponse = (Boolean) inputData.get("successResponse");
		
		if(successResponse == null || !successResponse)
		{
			String errorCode = (String) inputData.get("errorCode");
			Exception exception = (Exception) inputData.get("exception");
			String apiUrl = (String) inputData.get("apiUrl");
			Integer httpCode = (Integer) inputData.get("httpCode");
			
			if(errorCode != null)
			{
				if(errorCode.startsWith("C"))
				{
					String guiErrorMessage = coreFxController.getErrorsBundle().getString(errorCode);
					String logErrorMessage = coreFxController.getErrorsBundle().getString(errorCode + ".internal");
					
					guiErrorMessage = String.format(guiErrorMessage, httpCode);
					logErrorMessage = String.format(logErrorMessage, httpCode);
					
					if(exception != null) coreFxController.showErrorDialogAndWait(guiErrorMessage, exception);
					else showErrorNotification(guiErrorMessage);
					LOGGER.severe(logErrorMessage);
				}
				else if(errorCode.startsWith("B"))
				{
					String errorMessage = errorsBundle.getString(errorCode);
					showWarningNotification(errorMessage);
				}
				else // server error
				{
					String code = "S000-00000";
					String guiErrorMessage = coreFxController.getErrorsBundle().getString(code);
					String logErrorMessage = coreFxController.getErrorsBundle().getString(code + ".internal");
					
					guiErrorMessage = String.format(guiErrorMessage, errorCode);
					logErrorMessage = String.format(logErrorMessage, errorCode);
					
					showWarningNotification(guiErrorMessage);
					LOGGER.severe(logErrorMessage);
				}
			}
			else // the server didn't send an error code inside [400,401,403,500] response
			{
				String code = "C002-00018";
				String guiErrorMessage = coreFxController.getErrorsBundle().getString(code);
				String logErrorMessage = coreFxController.getErrorsBundle().getString(code + ".internal");
				logErrorMessage = String.format(logErrorMessage, apiUrl, String.valueOf(httpCode));
				
				showWarningNotification(guiErrorMessage);
				LOGGER.severe(logErrorMessage);
			}
		}
	}
	
	private void showNotification(String message, Image icon)
	{
		Platform.runLater(() ->
        {
            notificationPane.setGraphic(new ImageView(icon));
            notificationPane.show(message);
        });
	}
	
	@Override
	public void hideNotification()
	{
		notificationPane.hide();
	}
	
	@Override
	public void showSuccessNotification(String message)
	{
		showNotification(message, successIcon);
	}
	
	@Override
	public void showWarningNotification(String message)
	{
		showNotification(message, warningIcon);
	}
	
	@Override
	public void showErrorNotification(String message)
	{
		showNotification(message, errorIcon);
	}
}