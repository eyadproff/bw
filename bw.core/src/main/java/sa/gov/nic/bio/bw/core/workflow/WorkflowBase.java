package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The base class for all other workflow classes. It provides an implementation for <code>submitUserInput()</code>
 * and <code>renderUiAndWaitForUserInput()</code>.
 *
 * @author Fouad Almalki
 */
public abstract class WorkflowBase implements Workflow, AppLogger
{
	private int tabIndex;
	private Future<?> future;
	private Signal interruptionSignal;
	
	Long tcn;
	Map<String, Object> uiInputData = new HashMap<>();
	boolean renderedAtLeastOnceInTheStep = false;
	
	@Override
	public void setTabIndex(int tabIndex)
	{
		this.tabIndex = tabIndex;
	}
	
	@Override
	public int getTabIndex()
	{
		return tabIndex;
	}
	
	public Integer getWorkflowId()
	{
		AssociatedMenu annotation = getClass().getAnnotation(AssociatedMenu.class);
		return annotation != null ? annotation.workflowId() : null;
	}
	
	public Long getWorkflowTcn(){return tcn;}
	
	protected void throwInterruptionSignal() throws Signal
	{
		Signal temp = interruptionSignal;
		interruptionSignal = null;
		throw temp;
	}
	
	Signal getInterruptionSignal(){return interruptionSignal;}
	
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
				Context.getWorkflowManager().getUserTasks(getTabIndex()).put(uiDataMap);
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
	public void renderUiAndWaitForUserInput(Class<? extends ContentFxControllerBase> controllerClass)
																					throws InterruptedException, Signal
	{
		if(interruptionSignal != null) throwInterruptionSignal();
		
		Integer workflowId = getWorkflowId();
		
		if(workflowId != null) uiInputData.put(controllerClass.getName() + "#workflowId", workflowId);
		if(tcn != null) uiInputData.put(controllerClass.getName() + "#workflowTcn", tcn);
		
		ContentFxControllerBase currentBodyFxController = Context.getWorkflowManager().getFormRenderer()
		                                                         .renderForm(controllerClass, uiInputData, tabIndex);
		renderedAtLeastOnceInTheStep = true;
		
		Map<String, Object> uiDataMap;
		
		try
		{
			uiDataMap = Context.getWorkflowManager().getUserTasks(getTabIndex()).take();
		}
		catch(InterruptedException e)
		{
			if(interruptionSignal != null) throwInterruptionSignal();
			return;
		}
		
		if(interruptionSignal != null) throwInterruptionSignal();
		
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
			String errorCode = CoreErrorCodes.C002_00025.getCode();
			String[] errorDetails = {"Failure upon saving the workflow output! workflow = " +
							(currentBodyFxController != null ? currentBodyFxController.getClass().getName() : null)};
			Workflow.throwInvalidStateSignal(errorCode, errorDetails, e);
		}
	}
	
	@Override
	public void interrupt(Signal interruptionSignal)
	{
		this.interruptionSignal = interruptionSignal;
		
		if(future != null) future.cancel(true);
	}
	
	public void executeWorkflowTask(Class<? extends WorkflowTask> taskClass) throws Signal
	{
		Integer workflowId = getWorkflowId();
		
		try
		{
			if(workflowId != null) uiInputData.put(taskClass.getName() + "#workflowId", workflowId);
			if(tcn != null) uiInputData.put(taskClass.getName() + "#workflowTcn", tcn);
			
			WorkflowTask workflowTask = taskClass.getConstructor().newInstance();
			Workflow.loadWorkflowInputs(workflowTask, uiInputData, false, false);
			
			Throwable[] thrownException = new Throwable[1];
			Thread[] taskThread = new Thread[1];
			
			future = Context.getExecutorService().submit(() ->
			{
				taskThread[0] = Thread.currentThread();
				
				try
				{
					if(interruptionSignal != null) throwInterruptionSignal();
					
					if(Context.getCoreFxController().isMockTasksEnabled()) workflowTask.mockExecute();
					else workflowTask.execute();
				}
				catch(Throwable e)
				{
					thrownException[0] = e;
				}
				
				return null;
			});
			
			try
			{
				future.get();
			}
			catch(ExecutionException e)
			{
				throw e.getCause();
			}
			catch(CancellationException e)
			{
				LOGGER.info("The task (" + taskClass.getSimpleName() + ") is cancelled!");
				taskThread[0].interrupt();
				if(interruptionSignal != null) throwInterruptionSignal();
			}
			
			if(thrownException[0] != null) throw thrownException[0];
			
			Workflow.saveWorkflowOutputs(workflowTask, uiInputData);
		}
		catch(Throwable e)
		{
			if(e instanceof Signal) throw (Signal) e;
			
			String errorCode = CoreErrorCodes.C002_00026.getCode();
			String[] errorDetails = {"Failure upon executing workflow task! task = " +
																	(taskClass != null ? taskClass.getName() : null)};
			Workflow.throwInvalidStateSignal(errorCode, errorDetails, e);
		}
	}
}