package sa.gov.nic.bio.bw.client.features.cancelcriminal.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancellatent.CancelLatentPaneFxController;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class CancelCriminalWorkflow extends WorkflowBase<Void, Void>
{
	private static final Logger LOGGER = Logger.getLogger(CancelCriminalWorkflow.class.getName());
	
	public CancelCriminalWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		while(true)
		{
			formRenderer.renderForm(CancelLatentPaneFxController.class, null);
			
			// TODO: task service
			
			waitForUserTask();
		}
	}
}