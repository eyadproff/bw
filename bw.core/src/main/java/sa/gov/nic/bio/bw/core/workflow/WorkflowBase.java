package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;

import java.util.HashMap;
import java.util.Map;

/**
 * The base class for all other workflow classes. It provides an implementation for <code>submitUserInput()</code>
 * and <code>renderUiAndWaitForUserInput()</code>.
 *
 * @author Fouad Almalki
 */
public abstract class WorkflowBase implements Workflow, AppLogger
{
	Long tcn;
	Map<String, Object> uiInputData = new HashMap<>();
	boolean renderedAtLeastOnceInTheStep = false;
	
	/**
	 * Submit a user task to the workflow.
	 *
	 * @param uiDataMap the data submitted by the user when filling the form
	 */
	public void submitUserInput(Map<String, Object> uiDataMap)
	{
		Context.getExecutorService().submit(() ->
		{
			try
			{
				Context.getWorkflowManager().getUserTasks().put(uiDataMap);
			}
			catch(InterruptedException e) // most likely the executor service is shutting down
			{
				LOGGER.warning("The submission of a user task is interrupted!");
			}
		});
	}
	
	/**
	 * Renders the UI based on the passed controllerClass and then waits until the controller submits user input.
	 *
	 * @param controllerClass the controller class that will be used to render the new UI
	 *
	 * @throws InterruptedException thrown upon interrupting the workflow thread
	 * @throws Signal thrown in case the submitted user task is a signal
	 */
	public void renderUiAndWaitForUserInput(Class<? extends BodyFxControllerBase> controllerClass)
																					throws InterruptedException, Signal
	{
		BodyFxControllerBase currentBodyFxController = Context.getWorkflowManager().getFormRenderer()
																			.renderForm(controllerClass, uiInputData);
		renderedAtLeastOnceInTheStep = true;
		
		Map<String, Object> uiDataMap = Context.getWorkflowManager().getUserTasks().take();
		SignalType signalType = (SignalType) uiDataMap.get(KEY_SIGNAL_TYPE);
		
		if(signalType != null && signalType != SignalType.RESET_WORKFLOW_STEP)
							throw new Signal(signalType, uiDataMap); // menu navigation, wizard navigation and logout
		else uiInputData.putAll(uiDataMap);
		
		try
		{
			Workflow.saveWorkflowOutputs(currentBodyFxController, uiInputData);
			if(signalType == SignalType.RESET_WORKFLOW_STEP) throw new Signal(signalType, uiDataMap);
		}
		catch(Exception e)
		{
			String errorCode = CoreErrorCodes.C002_00029.getCode();
			String[] errorDetails = {"Failure upon saving the workflow output! workflow = " +
							(currentBodyFxController != null ? currentBodyFxController.getClass().getName() : null)};
			Map<String, Object> payload = new HashMap<>();
			payload.put(Workflow.KEY_ERROR_CODE, errorCode);
			payload.put(Workflow.KEY_EXCEPTION, e);
			payload.put(Workflow.KEY_ERROR_DETAILS, errorDetails);
			throw new Signal(SignalType.INVALID_STATE, payload);
		}
	}
	
	public void executeTask(Class<? extends WorkflowTask> taskClass) throws Signal
	{
		AssociatedMenu annotation = getClass().getAnnotation(AssociatedMenu.class);
		Integer workflowId = annotation != null ? annotation.workflowId() : null;
		
		try
		{
			WorkflowTask workflowTask = taskClass.getConstructor().newInstance();
			Workflow.loadWorkflowInputs(workflowTask, uiInputData, false, false);
			if(Context.getCoreFxController().isMockTasksEnabled()) workflowTask.mockExecute();
			else if(workflowId != null) workflowTask.execute(workflowId, tcn);
			else workflowTask.execute(null, null);
			Workflow.saveWorkflowOutputs(workflowTask, uiInputData);
		}
		catch(Exception e)
		{
			String errorCode = CoreErrorCodes.C002_00030.getCode();
			String[] errorDetails = {"Failure upon executing the workflow task! task = " +
																	(taskClass != null ? taskClass.getName() : null)};
			Map<String, Object> payload = new HashMap<>();
			payload.put(Workflow.KEY_ERROR_CODE, errorCode);
			payload.put(Workflow.KEY_EXCEPTION, e);
			payload.put(Workflow.KEY_ERROR_DETAILS, errorDetails);
			throw new Signal(SignalType.INVALID_STATE, payload);
		}
	}
}