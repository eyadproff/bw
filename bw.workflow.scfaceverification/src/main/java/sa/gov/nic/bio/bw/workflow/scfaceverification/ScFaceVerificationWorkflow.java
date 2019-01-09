package sa.gov.nic.bio.bw.workflow.scfaceverification;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.faceverification.controllers.MatchingFxController;
import sa.gov.nic.bio.bw.workflow.faceverification.controllers.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.scfaceverification.controllers.ShowResultFxController;
import sa.gov.nic.bio.bw.workflow.scfaceverification.tasks.ScFaceVerificationWorkflowTask;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ConfirmImageFxController;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(workflowId = 1015, menuId = "menu.securityClearance.faceVerification", menuTitle = "menu.title",
				menuOrder = 1, devices = Device.CAMERA)
@WithLookups(CountriesLookup.class)
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.personId"),
		@Step(iconId = "camera", title = "wizard.capturePhotoByCamera"),
		@Step(iconId = "unlock", title = "wizard.confirm"),
		@Step(iconId = "\\uf248", title = "wizard.matching"),
		@Step(iconId = "file_text_alt", title = "wizard.showResult")})
public class ScFaceVerificationWorkflow extends WizardWorkflowBase
{
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
	}
	
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
				renderUiAndWaitForUserInput(FaceCapturingFxController.class);
				break;
			}
			case 2:
			{
				passData(PersonIdPaneFxController.class, ConfirmImageFxController.class,
				         "personId");
				passData(FaceCapturingFxController.class, ConfirmImageFxController.class,
					         "facePhoto");
				renderUiAndWaitForUserInput(ConfirmImageFxController.class);
				break;
			}
			case 3:
			{
				renderUiAndWaitForUserInput(MatchingFxController.class);
				passData(PersonIdPaneFxController.class, ScFaceVerificationWorkflowTask.class,
				         "personId");
				passData(ConfirmImageFxController.class, ScFaceVerificationWorkflowTask.class,
				         "facePhotoBase64");
				executeWorkflowTask(ScFaceVerificationWorkflowTask.class);
				break;
			}
			case 4:
			{
				passData(PersonIdPaneFxController.class, ShowResultFxController.class,
				         "personId");
				passData(ConfirmImageFxController.class, ShowResultFxController.class,
				         "facePhoto");
				passData(ScFaceVerificationWorkflowTask.class, ShowResultFxController.class,
				         "faceMatchingResponse");
				renderUiAndWaitForUserInput(ShowResultFxController.class);
				break;
			}
		}
	}
}