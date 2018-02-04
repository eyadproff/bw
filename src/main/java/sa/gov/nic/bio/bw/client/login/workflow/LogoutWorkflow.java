package sa.gov.nic.bio.bw.client.login.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class LogoutWorkflow extends WorkflowBase<Void, Void>
{
	
	public LogoutWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input)
	{
		LogoutService.execute();
		return null;
	}
}