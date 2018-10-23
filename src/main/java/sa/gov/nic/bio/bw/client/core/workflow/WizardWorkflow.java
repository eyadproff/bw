package sa.gov.nic.bio.bw.client.core.workflow;

public interface WizardWorkflow extends Workflow<Void, Void>
{
	void onStep(int step) throws InterruptedException, Signal;
}