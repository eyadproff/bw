package sa.gov.nic.bio.bw.client.login.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class LogoutWorkflow extends WorkflowBase<Void, Boolean>
{
	
	public LogoutWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Boolean onProcess(Void input) throws InterruptedException
	{
		return Boolean.TRUE;
	}
}