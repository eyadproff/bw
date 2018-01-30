package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public abstract class WorkflowBase<I, O> implements Workflow<I, O>
{
	protected final FormRenderer formRenderer;
	protected BlockingQueue<Map<String, Object>> userTasks;
	
	public WorkflowBase(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		this.formRenderer = formRenderer;
		this.userTasks = userTasks;
	}
	
	@Override
	public void submitUserTask(Map<String, Object> uiDataMap) throws InterruptedException
	{
		userTasks.put(uiDataMap);
	}
}