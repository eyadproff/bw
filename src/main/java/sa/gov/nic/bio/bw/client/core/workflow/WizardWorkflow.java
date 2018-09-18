package sa.gov.nic.bio.bw.client.core.workflow;

public interface WizardWorkflow extends Workflow<Void, Void>
{
	boolean onStep(int step) throws InterruptedException, Signal;
}