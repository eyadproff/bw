package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * It manages starting the core workflow and submitting user tasks to the core workflow. A user task represents
 * the user action of submitting the forms that are shown on the GUI.
 *
 * @author Fouad Almalki
 */
public class WorkflowManager implements AppLogger
{
	private Workflow<Void, Void> coreWorkflow;
	private Thread workflowThread;
	protected FormRenderer formRenderer;
	protected BlockingQueue<Map<String, Object>> userTasks;
	
	public void setFormRenderer(FormRenderer formRenderer)
	{
		this.formRenderer = formRenderer;
	}
	
	/**
	 * Starts the core workflow and returns immediately.
	 *
	 * @param formRenderer the UI proxy that is responsible for rendering the forms on the GUI
	 */
	public void startCoreWorkflow(FormRenderer formRenderer)
	{
		if(isRunning())
		{
			LOGGER.warning("The workflow thread is already active!");
			return;
		}
		
		setFormRenderer(formRenderer);
		this.userTasks = new SynchronousQueue<>();
		
		workflowThread = new Thread(() ->
		{
			try
			{
				coreWorkflow = new CoreWorkflow();
				coreWorkflow.onProcess();
				
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
		
		coreWorkflow.submitUserInput(uiDataMap);
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
	
	public FormRenderer getFormRenderer(){return formRenderer;}
	
	public BlockingQueue<Map<String, Object>> getUserTasks(){return userTasks;}
}