package sa.gov.nic.bio.bw.client.core.workflow;

public interface WizardWorkflow<I, O> extends Workflow<I, O>
{
	void onStep(int step) throws InterruptedException, Signal;
}