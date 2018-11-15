package sa.gov.nic.bio.bw.core.controllers;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.NotificationController;
import sa.gov.nic.bio.bw.core.interfaces.WorkflowUserTaskController;
import sa.gov.nic.bio.bw.core.workflow.SignalType;
import sa.gov.nic.bio.bw.core.workflow.Workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A base class for any JavaFX controller that will be associated with the body region.
 *
 * @author Fouad Almalki
 */
public abstract class BodyFxControllerBase extends RegionFxControllerBase implements WorkflowUserTaskController,
																					 NotificationController
{
	private Image successIcon = new Image(BodyFxControllerBase.class
			                        .getResourceAsStream("/sa/gov/nic/bio/bw/core/images/success.png"));
	private Image warningIcon = new Image(BodyFxControllerBase.class
			                        .getResourceAsStream("/sa/gov/nic/bio/bw/core/images/warning.png"));
	private Image errorIcon = new Image(BodyFxControllerBase.class
			                        .getResourceAsStream("/sa/gov/nic/bio/bw/core/images/error.png"));
	
	private AtomicBoolean detached = new AtomicBoolean();
	
	public boolean isDetached(){return detached.get();}
	public void detach(){detached.set(true);}
	
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
			NotificationPane notificationPane = Context.getCoreFxController().getNotificationPane();
			
			if(notificationPane.isShowing()) notificationPane.hide();
		    notificationPane.setGraphic(new ImageView(icon));
		    notificationPane.show(message);
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void hideNotification()
	{
		Context.getCoreFxController().getNotificationPane().hide();
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
	 * A callback that is invoked after the root pane of this controller is attached to the scene.
	 */
	protected void onAttachedToScene(){}
	
	/**
	 * A callback that is invoked after the root pane of this controller is detached from the scene.
	 */
	protected void onDetachingFromScene(){}
	
	
	protected void reportNegativeTaskResponse(String errorCode, Throwable exception, String[] errorDetails)
	{
		if(errorCode.startsWith("B") || errorCode.startsWith("N")) // business error
		{
			// no exceptions/errorDetails in case of business error

			String guiErrorMessage = Context.getErrorsBundle().getString(errorCode);
			String logErrorMessage = Context.getErrorsBundle().getString(errorCode + ".internal");
			
			LOGGER.info(logErrorMessage);
			showWarningNotification(guiErrorMessage);
		}
		else // client error, server error, or unknown error
		{
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		}
	}
	
	protected void continueWorkflow()
	{
		if(!isDetached())
		{
			hideNotification();
			onShowingProgress(true);
			Context.getWorkflowManager().submitUserTask(new HashMap<>());
		}
	}
	
	protected void resetWorkflow()
	{
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(Workflow.KEY_SIGNAL_TYPE, SignalType.RESET_WORKFLOW_STEP);
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
}