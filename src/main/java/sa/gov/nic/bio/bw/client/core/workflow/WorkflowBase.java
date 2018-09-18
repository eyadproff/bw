package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * The base class for all other workflow classes. It provides an implementation for <code>submitUserInput()</code>
 * and <code>waitForUserInput()</code>.
 *
 * @param <I> type of the workflow's input. Use <code>Void</code> in case of not input
 * @param <O> type of the workflow's output. Use <code>Void</code> in case of no output
 *
 * @author Fouad Almalki
 */
public abstract class WorkflowBase<I, O> implements Workflow<I, O>
{
	private static final Logger LOGGER = Logger.getLogger(WorkflowBase.class.getName());
	
	protected final Map<String, Object> uiInputData = new HashMap<>();
	protected final AtomicReference<FormRenderer> formRenderer;
	protected final BlockingQueue<Map<String, Object>> userTasks;
	
	private BodyFxControllerBase currentBodyFxController;
	
	/**
	 * @param formRenderer the form renderer that will render the form on the screen
	 * @param userTasks the queue instance to hold the submitted user tasks
	 */
	public WorkflowBase(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		this.formRenderer = formRenderer;
		this.userTasks = userTasks;
	}
	
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
				userTasks.put(uiDataMap);
			}
			catch(InterruptedException e) // most likely the executor service is shutting down
			{
				LOGGER.warning("The submission of a user task is interrupted!");
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void waitForUserInput() throws InterruptedException, Signal
	{
		Map<String, Object> uiDataMap = userTasks.take();
		SignalType signalType = (SignalType) uiDataMap.get(KEY_SIGNAL_TYPE);
		
		if(signalType != null) throw new Signal(signalType, uiDataMap);
		else uiInputData.putAll(uiDataMap);
		
		try
		{
			Workflow.saveWorkflowOutputs(currentBodyFxController, uiInputData);
		}
		catch(Exception e)
		{
			Map<String, Object> payload = new HashMap<>();
			payload.put(KEY_ERROR_DETAILS, new String[]{"Failure upon saving the workflow output! workflow = " +
							(currentBodyFxController != null ? currentBodyFxController.getClass().getName() : null)});
			payload.put(KEY_EXCEPTION, e);
			throw new Signal(SignalType.INVALID_STATE, payload);
		}
	}
	
	@Override
	public void renderUi(Class<? extends BodyFxControllerBase> controllerClass) throws Signal
	{
		currentBodyFxController = formRenderer.get().renderForm(controllerClass, uiInputData);
	}
	
	@Override
	public void executeTask(Class<? extends WorkflowTask> taskClass) throws Signal
	{
		try
		{
			WorkflowTask workflowTask = taskClass.newInstance();
			Workflow.loadWorkflowInputs(workflowTask, uiInputData, false);
			ServiceResponse<?> serviceResponse = workflowTask.execute();
			uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
			Workflow.saveWorkflowOutputs(workflowTask, uiInputData);
			
			if(!serviceResponse.isSuccess()) throw new Signal(SignalType.INTERRUPT_SEQUENT_TASKS, null);
		}
		catch(Exception e)
		{
			Map<String, Object> payload = new HashMap<>();
			payload.put(KEY_ERROR_DETAILS, new String[]{"Failure upon executing the workflow task! task = " +
																(taskClass != null ? taskClass.getName() : null)});
			payload.put(KEY_EXCEPTION, e);
			throw new Signal(SignalType.INVALID_STATE, payload);
		}
	}
}