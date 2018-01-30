package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.client.core.interfaces.BodyFxController;
import sa.gov.nic.bio.bw.client.core.interfaces.ResourceBundleCollection;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;
import sa.gov.nic.bio.bw.client.login.workflow.WebServiceResponse;

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
	public void onReturnFromServiceTask(boolean firstVisit, Map<String, Object> dataMap)
	{
		if(!firstVisit)
		{
			ServiceResponse<?> serviceResponse = (ServiceResponse<?>) dataMap.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			if(!serviceResponse.isSuccess())
			{
				String errorCode = serviceResponse.getErrorCode();
				Exception exception = serviceResponse.getException();
				String apiUrl = null;
				int httpCode = 0;
				
				if(serviceResponse instanceof WebServiceResponse)
				{
					WebServiceResponse webServiceResponse = (WebServiceResponse) serviceResponse;
					
					apiUrl = webServiceResponse.getUrl();
					httpCode = webServiceResponse.getHttpCode();
				}
				
				if(errorCode != null)
				{
					if(errorCode.startsWith("C"))
					{
						String guiErrorMessage = coreFxController.getErrorsBundle().getString(errorCode);
						String logErrorMessage = coreFxController.getErrorsBundle().getString(errorCode + ".internal");
						
						guiErrorMessage = httpCode > 0 ? String.format(guiErrorMessage, apiUrl, httpCode) : String.format(guiErrorMessage, apiUrl);
						logErrorMessage = httpCode > 0 ? String.format(logErrorMessage, apiUrl, httpCode) : String.format(logErrorMessage, apiUrl);
						
						if(exception != null) coreFxController.showErrorDialogAndWait(guiErrorMessage, exception);
						else showErrorNotification(guiErrorMessage);
						LOGGER.severe(logErrorMessage);
					}
					else if(errorCode.startsWith("B"))
					{
						String guiErrorMessage;
						String logErrorMessage;
						
						if(errorCode.startsWith("B010"))
						{
							guiErrorMessage = coreFxController.getErrorsBundle().getString(errorCode);
							logErrorMessage = coreFxController.getErrorsBundle().getString(errorCode + ".internal");
						}
						else
						{
							guiErrorMessage = errorsBundle.getString(errorCode);
							logErrorMessage = errorsBundle.getString(errorCode + ".internal");
						}
						
						showWarningNotification(guiErrorMessage);
						LOGGER.info(logErrorMessage);
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
					guiErrorMessage = String.format(guiErrorMessage, apiUrl, String.valueOf(httpCode));
					logErrorMessage = String.format(logErrorMessage, apiUrl, String.valueOf(httpCode));
					
					showWarningNotification(guiErrorMessage);
					LOGGER.severe(logErrorMessage);
				}
			}
		}
	}
	
	private void showNotification(String message, Image icon)
	{
		Platform.runLater(() ->
        {
            coreFxController.getNotificationPane().setGraphic(new ImageView(icon));
	        coreFxController.getNotificationPane().show(message);
        });
	}
	
	@Override
	public void hideNotification()
	{
		coreFxController.getNotificationPane().hide();
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