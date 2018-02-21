package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.client.core.interfaces.NotificationController;
import sa.gov.nic.bio.bw.client.core.interfaces.WorkflowUserTaskController;

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
	
	
	protected void reportNegativeResponse(String errorCode, Exception exception, String[] errorDetails)
	{
		if(errorCode.startsWith("B") || errorCode.startsWith("N")) // business error
		{
			// no exceptions/errorDetails in case of business error
			
			if(errorCode.startsWith("N")) errorCode = errorCode.replace("-", "_");
			
			String guiErrorMessage = Context.getErrorsBundle().getString(errorCode);
			String logErrorMessage = Context.getErrorsBundle().getString(errorCode + ".internal");
			
			LOGGER.info(logErrorMessage);
			showWarningNotification(guiErrorMessage);
		}
		else // client error, server error, or unknown error
		{
			coreFxController.showErrorDialog(errorCode, exception, errorDetails);
		}
	}
}