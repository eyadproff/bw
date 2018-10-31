package sa.gov.nic.bio.bw.client.core.workflow;

public abstract class SinglePageWorkflowBase extends WizardWorkflowBase
{
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		onStep();
	}
	
	public abstract void onStep() throws InterruptedException, Signal;
}