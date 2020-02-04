package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.FingerprintsSourceFxController;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.MiscreantIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.ShowingMiscreantInfoFxController;

@AssociatedMenu(workflowId = 1025, menuId = "menu.register.miscreantFingerprintsEnrollment",
				menuTitle = "menu.title", menuOrder = 8, devices = {Device.BIO_UTILITIES})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.InquiryByMiscreantId"),
		 @Step(iconId = "database", title = "wizard.inquiryResult"),
		 @Step(iconId = "question", title = "wizard.selectFingerprintsSource"),
		 @Step(iconId = "upload", title = "wizard.uploadNistFile"),
		 @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
		 @Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
		 @Step(iconId = "save", title = "wizard.registerFingerprints")})
public class MiscreantFingerprintsEnrollmentWorkflow extends WizardWorkflowBase
{
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(MiscreantIdPaneFxController.class);
				break;
			}
			case 1:
			{
				renderUiAndWaitForUserInput(ShowingMiscreantInfoFxController.class);
				break;
			}
			case 2:
			{
				renderUiAndWaitForUserInput(FingerprintsSourceFxController.class);
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
		}
	}
}