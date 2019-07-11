package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.interfaces.FormRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * It manages starting the core workflow and submitting user tasks to the core workflow. A user task represents
 * the user action of submitting the forms that are shown on the GUI.
 *
 * @author Fouad Almalki
 */
public class WorkflowManager implements AppLogger
{
	private FormRenderer formRenderer;
	
	private Map<Integer, CoreWorkflow> coreWorkflows = new HashMap<>();
	private Map<Integer, Thread> workflowThreads = new HashMap<>();
	private Map<Integer, BlockingQueue<Map<String, Object>>> userTasksList = new HashMap<>();
	private Map<Integer, Workflow> currentWorkflows = new HashMap<>();
	
	public void setFormRenderer(FormRenderer formRenderer){this.formRenderer = formRenderer;}
	public FormRenderer getFormRenderer(){return formRenderer;}
	
	public void setCurrentWorkflow(Workflow currentWorkflow, int index){this.currentWorkflows.put(index, currentWorkflow);}
	public Workflow getCurrentWorkflow(int index){return currentWorkflows.get(index);}
	public BlockingQueue<Map<String, Object>> getUserTasks(int index){return userTasksList.get(index);}
	
	/**
	 * Starts the core workflow and returns immediately.
	 *
	 * @param formRenderer the UI proxy that is responsible for rendering the forms on the GUI
	 */
	public void startCoreWorkflow(FormRenderer formRenderer, int index)
	{
		if(isRunning(index))
		{
			LOGGER.warning("The workflow thread #" + index + " is already active!");
			return;
		}
		
		this.formRenderer = formRenderer;
		userTasksList.put(index, new LinkedBlockingQueue<>());
		
		Thread workflowThread = new Thread(() ->
		{
			try
			{
				CoreWorkflow coreWorkflow = new CoreWorkflow();
				coreWorkflow.setTabIndex(index);
				coreWorkflow.setLoggedIn(index > 0);
				coreWorkflows.put(index, coreWorkflow);
				
				LOGGER.info("The core workflow #" + index + " is started!");
				
				coreWorkflow.onProcess(null);
				
				LOGGER.info("The core workflow #" + index + " is finished.");
			}
			catch(InterruptedException e)
			{
				LOGGER.info("The core workflow #" + index + " is interrupted.");
			}
		});
		
		workflowThreads.put(index, workflowThread);
		workflowThread.setName("Workflow Thread #" + index);
		workflowThread.setDaemon(true);
		workflowThread.start();
	}
	
	/**
	 * Submit a user task to the core workflow.
	 *
	 * @param uiDataMap a map that contains user entries
	 */
	public void submitUserTask(Map<String, Object> uiDataMap, int index)
	{
		CoreWorkflow coreWorkflow = coreWorkflows.get(index);
		
		if(coreWorkflow == null)
		{
			LOGGER.warning("The core workflow #" + index + " is not yet initiated!");
			return;
		}
		
		coreWorkflow.submitUserInput(uiDataMap);
	}
	
	/**
	 * Interrupt the core workflow. Useful on exiting the application.
	 */
	public synchronized void interruptCoreWorkflow(int index)
	{
		Thread workflowThread = workflowThreads.get(index);
		Workflow currentWorkflow = currentWorkflows.get(index);
		
		if(workflowThread == null)
		{
			LOGGER.warning("The workflow thread #" + index + " is not yet initiated!");
			return;
		}
		else if(!workflowThread.isAlive())
		{
			LOGGER.warning("The workflow thread #" + index + " is not active!");
			return;
		}
		
		currentWorkflow.interrupt(new Signal(SignalType.EXIT_WORKFLOW));
		workflowThread.interrupt();
		
		coreWorkflows.remove(index);
		workflowThreads.remove(index);
		userTasksList.remove(index);
		currentWorkflows.remove(index);
	}
	
	public synchronized void interruptAllCoreWorkflows()
	{
		for(Integer index : workflowThreads.keySet()) interruptCoreWorkflow(index);
	}
	
	public boolean isRunning(int index)
	{
		Thread workflowThread = workflowThreads.get(index);
		return workflowThread != null && workflowThread.isAlive();
	}
	
	public void interruptCurrentWorkflow(Signal interruptionSignal, int index)
	{
		Workflow currentWorkflow = currentWorkflows.get(index);
		
		if(currentWorkflow != null)
		{
			currentWorkflow.interrupt(interruptionSignal);
			submitUserTask(new HashMap<>(), index); // in case the workflow is waiting at queue.take()
		}
	}
}