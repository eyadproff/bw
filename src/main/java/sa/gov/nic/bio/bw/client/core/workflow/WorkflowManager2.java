package sa.gov.nic.bio.bw.client.core.workflow;

import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowManager2
{
	private static final Logger LOGGER = Logger.getLogger(WorkflowManager2.class.getName());
	private Workflow<Void, Void> currentWorkflow;
	private Thread workflowThread;
	
	public void load()
	{
		URL[] urls = ClasspathUrlFinder.findClassPaths(); // scan java.class.path
		AnnotationDB db = new AnnotationDB();
		//db.scanArchives(urls);
		//Set<String> entityClasses = db.getAnnotationIndex().get(MyOwnAnnotation.class.getName());
	}
	
	public void interruptTheWorkflow()
	{
		if(workflowThread != null) workflowThread.interrupt();
	}
	
	public void startProcess(FormRenderer formRenderer)
	{
		workflowThread = new Thread(() ->
		{
			try
			{
				currentWorkflow = new CoreWorkflow(formRenderer, new SynchronousQueue<>());
				currentWorkflow.onProcess(null);
				
				LOGGER.info("The core workflow is finished.");
			}
			catch(InterruptedException e)
			{
				LOGGER.info("The core workflow is interrupted.");
			}
			catch(Signal signal)
			{
				signal.printStackTrace(); // TODO
			}
		});
		
		workflowThread.start();
	}
	
	public void submitFormTask(Map<String, Object> uiDataMap)
	{
		try
		{
			currentWorkflow.submitUserTask(uiDataMap);
		}
		catch(InterruptedException e) // should never happen
		{
			LOGGER.log(Level.SEVERE, "WorkflowManager.submitFormTask() is interrupted", e);
		}
	}
}