package sa.gov.nic.bio.bw.workflow.registeriris;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.registeriris.controllers.PersonIdPaneFxController;

@AssociatedMenu(workflowId = 1021, menuId = "menu.register.registerIris",
				menuTitle = "menu.title", menuOrder = 7, devices = {Device.FINGERPRINT_SCANNER,
				                                                    Device.IRIS_SCANNER, Device.CAMERA})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
        @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
        @Step(iconId = "question", title = "wizard.selectVerificationMethod"),
		@Step(iconId = "\\uf25a", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "eye", title = "wizard.irisCapturing"),
		@Step(iconId = "spinner", title = "wizard.enrollment"),
		@Step(iconId = "\\uf0f6", title = "wizard.enrollmentResult")})
public class RegisterIrisWorkflow extends WizardWorkflowBase
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
		}
	}
}