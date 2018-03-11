package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;

import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * It manages starting the core workflow and submitting user tasks to the core workflow. A user task represents
 * the user action of submitting the forms that are shown on the GUI.
 *
 * @author Fouad Almalki
 */
public class WorkflowManager
{
	private static final Logger LOGGER = Logger.getLogger(WorkflowManager.class.getName());
	private Workflow<Void, Void> coreWorkflow;
	private Thread workflowThread;
	private AtomicReference<FormRenderer> formRendererReference = new AtomicReference<>();
	
	public void changeFormRenderer(FormRenderer formRenderer)
	{
		this.formRendererReference.set(formRenderer);
	}
	
	/**
	 * Starts the core workflow and returns immediately.
	 *
	 * @param formRenderer the UI proxy that is responsible for rendering the forms on the GUI
	 */
	public synchronized void startCoreWorkflow(FormRenderer formRenderer)
	{
		formRendererReference.set(formRenderer);
		
		if(isRunning())
		{
			LOGGER.warning("The workflow thread is already active!");
			return;
		}
		
		workflowThread = new Thread(() ->
		{
			try
			{
				coreWorkflow = new CoreWorkflow(formRendererReference, new SynchronousQueue<>());
				coreWorkflow.onProcess(null);
				
				LOGGER.info("The core workflow is finished.");
			}
			catch(InterruptedException e)
			{
				LOGGER.info("The core workflow is interrupted.");
			}
			catch(Signal signal) // we don't use signals on the core workflow level
			{
				LOGGER.severe("The core workflow is interrupted by a signal! " + signal);
			}
		});
		
		workflowThread.start();
	}
	
	/**
	 * Submit a user task to the core workflow.
	 *
	 * @param uiDataMap a map that contains user entries
	 */
	public void submitUserTask(Map<String, Object> uiDataMap)
	{
		if(coreWorkflow == null)
		{
			LOGGER.warning("The core workflow is not yet initiated!");
			return;
		}
		
		coreWorkflow.submitUserTask(uiDataMap);
	}
	
	/**
	 * Interrupt the core workflow. Useful on exiting the application.
	 */
	public synchronized void interruptTheWorkflow()
	{
		if(workflowThread == null)
		{
			LOGGER.warning("The workflow thread is not yet initiated!");
			return;
		}
		else if(!workflowThread.isAlive())
		{
			LOGGER.warning("The workflow thread is not active!");
			return;
		}
		
		workflowThread.interrupt();
	}
	
	public boolean isRunning()
	{
		return workflowThread != null && workflowThread.isAlive();
	}
}