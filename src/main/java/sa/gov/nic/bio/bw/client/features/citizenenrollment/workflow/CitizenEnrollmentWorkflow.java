package sa.gov.nic.bio.bw.client.features.citizenenrollment.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.citizenenrollment.PersonIdPaneFxController;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CitizenEnrollmentWorkflow extends WizardWorkflowBase<Void, Void>
{
	public CitizenEnrollmentWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                 BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		Map<String, Object> uiOutputData = null;
		
		switch(step)
		{
			case 0:
			{
				renderUi(PersonIdPaneFxController.class);
				waitForUserInput();
				break;
			}
			case 1:
			{
				break;
			}
			case 2:
			{
				break;
			}
			case 3:
			{
				break;
			}
			case 4:
			{
				break;
			}
			case 5:
			{
				break;
			}
			case 6:
			{
				break;
			}
			default:
			{
				waitForUserInput();
				break;
			}
		}
	}
}