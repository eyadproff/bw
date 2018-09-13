package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;

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
	}
	
	@Override
	public void renderUi(Class<? extends BodyFxControllerBase> controllerClass)
	{
		formRenderer.get().renderForm(controllerClass, uiInputData);
	}
}