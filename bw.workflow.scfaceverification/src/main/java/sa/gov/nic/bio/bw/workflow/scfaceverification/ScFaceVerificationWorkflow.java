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
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController.Source;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.UploadImageFileFxController;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(workflowId = 1015, menuId = "menu.securityClearance.faceVerification", menuTitle = "menu.title",
				menuOrder = 1, devices = Device.CAMERA)
@WithLookups(CountriesLookup.class)
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.personId"),
		@Step(iconId = "question", title = "wizard.imageSource"),
		@Step(iconId = "upload", title = "wizard.uploadImage"),
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
				renderUiAndWaitForUserInput(ImageSourceFxController.class);
				break;
			}
			case 2:
			{
				Source imageSource = getData(ImageSourceFxController.class, "imageSource");
				
				if(Source.UPLOAD.equals(imageSource))
				{
					renderUiAndWaitForUserInput(UploadImageFileFxController.class);
				}
				else if(Source.CAMERA.equals(imageSource))
				{
					renderUiAndWaitForUserInput(FaceCapturingFxController.class);
				}
				
				break;
			}
			case 3:
			{
				Source imageSource = getData(ImageSourceFxController.class, "imageSource");
				
				passData(PersonIdPaneFxController.class, ConfirmImageFxController.class,
				         "personId");
				
				if(Source.UPLOAD.equals(imageSource))
				{
					passData(UploadImageFileFxController.class, "uploadedImage",
					         ConfirmImageFxController.class, "facePhoto");
				}
				else if(Source.CAMERA.equals(imageSource))
				{
					passData(FaceCapturingFxController.class, ConfirmImageFxController.class,
					         "facePhoto");
				}
				
				renderUiAndWaitForUserInput(ConfirmImageFxController.class);
				break;
			}
			case 4:
			{
				renderUiAndWaitForUserInput(MatchingFxController.class);
				
				passData(PersonIdPaneFxController.class, ScFaceVerificationWorkflowTask.class,
				         "personId");
				passData(ConfirmImageFxController.class, ScFaceVerificationWorkflowTask.class,
				         "facePhotoBase64");
				executeWorkflowTask(ScFaceVerificationWorkflowTask.class);
				
				break;
			}
			case 5:
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