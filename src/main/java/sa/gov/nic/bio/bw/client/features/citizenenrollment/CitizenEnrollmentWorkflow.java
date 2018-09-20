package sa.gov.nic.bio.bw.client.features.citizenenrollment;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.citizenenrollment.controllers.PersonIdPaneFxController;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.register.citizenEnrollment", title = "menu.title", order = 1)
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.InquiryByPersonId"),
		@Step(iconId = "database", title = "wizard.inquiryResult"),
		@Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "camera", title = "wizard.facePhotoCapturing"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "spinner", title = "wizard.enrollment"),
		@Step(iconId = "\\uf0f6", title = "wizard.enrollmentResult")})
public class CitizenEnrollmentWorkflow extends WizardWorkflowBase
{
	public CitizenEnrollmentWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                 BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public boolean onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUi(PersonIdPaneFxController.class);
				waitForUserInput();
				return true;
			}
			case 1:
			{
				return false;
			}
			case 2:
			{
				return false;
			}
			case 3:
			{
				return false;
			}
			case 4:
			{
				return false;
			}
			case 5:
			{
				return false;
			}
			case 6:
			{
				return false;
			}
			default: return false;
		}
	}
}