package sa.gov.nic.bio.bw.client.features.citizenenrollment;

import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.citizenenrollment.controllers.PersonIdPaneFxController;

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
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
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
			default: break;
		}
	}
}