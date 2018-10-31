package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.client.core.utils.WithResourceBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * The base class for all other workflow classes. It provides an implementation for <code>submitUserInput()</code>
 * and <code>renderUiAndWaitForUserInput()</code>.
 *
 * @param <I> type of the workflow's input. Use <code>Void</code> in case of not input
 * @param <O> type of the workflow's output. Use <code>Void</code> in case of no output
 *
 * @author Fouad Almalki
 */
@WithResourceBundle
public abstract class WorkflowBase<I, O> implements Workflow<I, O>
{
	protected final Map<String, Object> uiInputData = new HashMap<>();
	
	boolean renderedAtLeastOnceInTheStep = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
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
	
	@Override
	public void renderUiAndWaitForUserInput(Class<? extends BodyFxControllerBase> controllerClass)
																					throws InterruptedException, Signal
	{
		BodyFxControllerBase currentBodyFxController = Context.getWorkflowManager().getFormRenderer()
																			.renderForm(controllerClass, uiInputData);
		renderedAtLeastOnceInTheStep = true;
		
		Map<String, Object> uiDataMap = Context.getWorkflowManager().getUserTasks().take();
		SignalType signalType = (SignalType) uiDataMap.get(KEY_SIGNAL_TYPE);
		
		if(signalType != null) throw new Signal(signalType, uiDataMap); // menu navigation, wizard navigation and logout
		else uiInputData.putAll(uiDataMap);
		
		try
		{
			Workflow.saveWorkflowOutputs(currentBodyFxController, uiInputData);
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
	
	@Override
	public void executeTask(Class<? extends WorkflowTask> taskClass) throws Signal
	{
		try
		{
			WorkflowTask workflowTask = taskClass.newInstance();
			Workflow.loadWorkflowInputs(workflowTask, uiInputData, false, false);
			workflowTask.execute();
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