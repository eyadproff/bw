package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.client.core.interfaces.NotificationController;
import sa.gov.nic.bio.bw.client.core.interfaces.WorkflowUserTaskController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;
import sa.gov.nic.bio.bw.client.login.workflow.WebServiceResponse;

import java.util.logging.Logger;

/**
 * A base class for any JavaFX controller that will be associated with the body region.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public abstract class BodyFxControllerBase extends RegionFxControllerBase implements WorkflowUserTaskController,
																					 NotificationController
{
	private static final Logger LOGGER = Logger.getLogger(BodyFxControllerBase.class.getName());
	
	private Image successIcon = new Image(Thread.currentThread().getContextClassLoader()
			                        .getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/success.png"));
	private Image warningIcon = new Image(Thread.currentThread().getContextClassLoader()
			                        .getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/warning.png"));
	private Image errorIcon = new Image(Thread.currentThread().getContextClassLoader()
			                        .getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/error.png"));
	
	/**
	 * Show a sliding notification message in the top edge of the body region.
	 *
	 * @param message the message to show on the notification bar
	 * @param icon the icon to show on the notification bar
	 */
	private void showNotification(String message, Image icon)
	{
		Platform.runLater(() ->
		{
		    coreFxController.getNotificationPane().setGraphic(new ImageView(icon));
		    coreFxController.getNotificationPane().show(message);
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void hideNotification()
	{
		coreFxController.getNotificationPane().hide();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void showSuccessNotification(String message)
	{
		showNotification(message, successIcon);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void showWarningNotification(String message)
	{
		showNotification(message, warningIcon);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void showErrorNotification(String message)
	{
		showNotification(message, errorIcon);
	}
	
	/**
	 * A callback that is invoked after the controller is completely initialized. The core controller and the resource
	 * bundles are attached to this controller before calling <code>onControllerReady()</code>.
	 */
	public void onControllerReady(){}
	
	
	protected void handleNegativeResponse(ServiceResponse<?> serviceResponse)
	{
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
						guiErrorMessage = coreFxController.getErrorsBundle().getString(errorCode);
						logErrorMessage = coreFxController.getErrorsBundle().getString(errorCode + ".internal");
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