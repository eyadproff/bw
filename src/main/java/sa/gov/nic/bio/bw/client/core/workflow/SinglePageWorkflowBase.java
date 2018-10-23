package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SinglePageWorkflowBase extends WizardWorkflowBase
{
	public SinglePageWorkflowBase(AtomicReference<FormRenderer> formRenderer,
	                              BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		onStep();
	}
	
	public abstract void onStep() throws InterruptedException, Signal;
}