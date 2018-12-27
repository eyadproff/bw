package sa.gov.nic.bio.bw.core.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.NotificationController;
import sa.gov.nic.bio.bw.core.interfaces.WorkflowUserTaskController;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.core.workflow.DataConveyor;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SignalType;
import sa.gov.nic.bio.bw.core.workflow.Workflow;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * A base class for any JavaFX controller that will be associated with the body region.
 *
 * @author Fouad Almalki
 */
public abstract class BodyFxControllerBase extends RegionFxControllerBase implements WorkflowUserTaskController,
																					 NotificationController,
																					 DataConveyor
{
	public static abstract class SuccessHandler
	{
		private Map<String, Object> dataMap;
		private Class<? extends WorkflowTask> taskClass;
		
		public void setDataMap(Map<String, Object> dataMap){this.dataMap = dataMap;}
		public void setTaskClass(Class<? extends WorkflowTask> taskClass){this.taskClass = taskClass;}
		
		protected abstract void onSuccess();
		
		@SuppressWarnings("unchecked")
		protected <T> T getData(String outputName)
		{
			return (T) dataMap.get(taskClass.getName() + "#" + outputName);
		}
	}
	
	public interface FailureHandler
	{
		void onFailure(Throwable throwable);
	}
	
	@Input protected Integer workflowId;
	@Input protected Long workflowTcn;
	
	private Image successIcon = new Image(CommonImages.ICON_SUCCESS_32PX.getAsInputStream());
	private Image warningIcon = new Image(CommonImages.ICON_WARNING_32PX.getAsInputStream());
	private Image errorIcon = new Image(CommonImages.ICON_ERROR_32PX.getAsInputStream());
	
	private AtomicBoolean detached = new AtomicBoolean();
	private Map<String, Object> uiInputData = new HashMap<>();
	
	public boolean isDetached(){return detached.get();}
	public void detach(){detached.set(true);}
	
	@Override
	public Map<String, Object> getDataMap()
	{
		return uiInputData;
	}
	
	public boolean executeUiTask(Class<? extends WorkflowTask> taskClass, SuccessHandler successHandler,
	                          FailureHandler failureHandler)
	{
		try
		{
			if(workflowId != null) uiInputData.put(taskClass.getName() + "#workflowId", workflowId);
			if(workflowTcn != null) uiInputData.put(taskClass.getName() + "#workflowTcn", workflowTcn);
			
			successHandler.setDataMap(uiInputData);
			successHandler.setTaskClass(taskClass);
			
			WorkflowTask workflowTask = taskClass.getConstructor().newInstance();
			Workflow.loadWorkflowInputs(workflowTask, uiInputData, false, false);
			Task<Void> task = new Task<>()
			{
				@Override
				protected Void call() throws Exception
				{
					try
					{
						if(Context.getCoreFxController().isMockTasksEnabled()) workflowTask.mockExecute();
						else workflowTask.execute();
					}
					catch(Exception e)
					{
						throw e;
					}
					catch(Throwable e)
					{
						throw new Exception(e);
					}
					
					return null;
				}
			};
			task.setOnSucceeded(event ->
			{
				try
				{
					Workflow.saveWorkflowOutputs(workflowTask, uiInputData);
					successHandler.onSuccess();
				}
				catch(Exception e)
				{
					String errorCode = CoreErrorCodes.C002_00027.getCode();
					String[] errorDetails = {"Failure upon saving outputs of a task! task = " + taskClass.getName()};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			});
			task.setOnFailed(event ->
			{
			    Throwable exception = task.getException();
			    if(exception instanceof ExecutionException) exception = exception.getCause();
			    failureHandler.onFailure(exception);
			});
			Context.getExecutorService().submit(task);
			return true;
		}
		catch(Signal signal)
		{
			String errorCode;
			Exception exception;
			String[] errorDetails;
			
			Map<String, Object> payload = signal.getPayload();
			if(payload != null)
			{
				errorCode = (String) payload.get(Workflow.KEY_ERROR_CODE);
				exception = (Exception) payload.get(Workflow.KEY_EXCEPTION);
				errorDetails = (String[]) payload.get(Workflow.KEY_ERROR_DETAILS);
				
				if(errorCode == null) errorCode = CoreErrorCodes.C002_00028.getCode();
			}
			else
			{
				errorCode = CoreErrorCodes.C002_00029.getCode();
				exception = null;
				errorDetails = new String[]{"Failure upon loading UI task! task = " + taskClass.getName()};
				Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
			}
			
			if(errorDetails == null) errorDetails = new String[0];
			
			// expand the array
			String[] newArray = new String[errorDetails.length + 1];
			System.arraycopy(errorDetails, 0, newArray, 0, errorDetails.length);
			errorDetails = newArray;
			
			errorDetails[errorDetails.length - 1] = "SignalType = " + signal.getSignalType();
			
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		}
		catch(Throwable e)
		{
			String errorCode = CoreErrorCodes.C002_00030.getCode();
			String[] errorDetails = {"Failure upon loading UI task! task = " +
																	(taskClass != null ? taskClass.getName() : null)};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
		}
		
		return false;
	}
	
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
			
			try
			{
				String guiErrorMessage = Context.getErrorsBundle().getString(errorCode);
				String logErrorMessage = Context.getErrorsBundle().getString(errorCode + ".internal");
				
				LOGGER.info(logErrorMessage);
				showWarningNotification(guiErrorMessage);
			}
			catch(Exception e)
			{
				LOGGER.log(Level.WARNING, errorCode, e);
				showWarningNotification(errorCode);
			}
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