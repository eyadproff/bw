package sa.gov.nic.bio.bw.workflow.registeriris;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingPersonInfoFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SingleFingerprintCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registeriris.controllers.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.registeriris.controllers.VerificationMethodSelectionFxController;
import sa.gov.nic.bio.bw.workflow.registeriris.controllers.VerificationMethodSelectionFxController.VerificationMethod;

@AssociatedMenu(workflowId = 1021, menuId = "menu.register.registerIris",
				menuTitle = "menu.title", menuOrder = 7, devices = {Device.FINGERPRINT_SCANNER,
				                                                    Device.IRIS_SCANNER, Device.CAMERA})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
        @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
        @Step(iconId = "question", title = "wizard.selectVerificationMethod"),
		@Step(iconId = "\\uf25a", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "\\uf2b5", title = "wizard.verification"),
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
				passData(PersonIdPaneFxController.class, GetPersonInfoByIdWorkflowTask.class,
				         "personId");
				executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
				break;
			}
			case 1:
			{
				passData(GetPersonInfoByIdWorkflowTask.class, ShowingPersonInfoFxController.class,
				         "personInfo");
				
				renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);
				break;
			}
			case 2:
			{
				renderUiAndWaitForUserInput(VerificationMethodSelectionFxController.class);
				break;
			}
			case 3:
			{
				VerificationMethod verificationMethod = getData(VerificationMethodSelectionFxController.class,
				                                                "verificationMethod");
				
				if(VerificationMethod.FINGERPRINT.equals(verificationMethod))
				{
					setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprint",
					        Boolean.TRUE);
					setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
					        0);
					renderUiAndWaitForUserInput(SingleFingerprintCapturingFxController.class);
				}
				else if(VerificationMethod.FACE_PHOTO.equals(verificationMethod))
				{
					setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);
					renderUiAndWaitForUserInput(FaceCapturingFxController.class);
				}
				
				break;
			}
		}
	}
}