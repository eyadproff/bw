package sa.gov.nic.bio.bw.client.core.workflow;

import java.util.Map;

public interface WizardWorkflow<I, O> extends Workflow<I, O>
{
	default void init() throws InterruptedException, Signal{};
	Map<String, Object> onStep(int step) throws InterruptedException, Signal;
}