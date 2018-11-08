package sa.gov.nic.bio.bw.features.searchbyfaceimage;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.features.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers.ConfirmImageFxController;
import sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers.ImageSourceFxController;
import sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers.ImageSourceFxController.Source;
import sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers.SearchFxController;
import sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers.ShowResultsFxController;
import sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers.UploadImageFileFxController;
import sa.gov.nic.bio.bw.features.searchbyfaceimage.workflow.SearchByFaceImageWorkflowTask;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(id = "menu.query.searchByFaceImage", title = "menu.title", order = 1, devices = Device.CAMERA)
@Wizard({@Step(iconId = "question", title = "wizard.imageSource"),
		@Step(iconId = "upload", title = "wizard.uploadImage"),
		@Step(iconId = "unlock", title = "wizard.confirm"),
		@Step(iconId = "search", title = "wizard.search"),
		@Step(iconId = "users", title = "wizard.showResults")})
public class SearchByFaceImageWorkflow extends WizardWorkflowBase
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
				setData(ImageSourceFxController.class, "hidePreviousButton", true);
				renderUiAndWaitForUserInput(ImageSourceFxController.class);
				break;
			}
			case 1:
			{
				Source imageSource = getData(ImageSourceFxController.class, "imageSource");
				
				if(Source.CAMERA.equals(imageSource))
				{
					setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);
					renderUiAndWaitForUserInput(FaceCapturingFxController.class);
				}
				else if(Source.UPLOAD.equals(imageSource))
				{
					renderUiAndWaitForUserInput(UploadImageFileFxController.class);
				}
				
				break;
			}
			case 2:
			{
				Source imageSource = getData(ImageSourceFxController.class, "imageSource");
				
				if(Source.UPLOAD.equals(imageSource))
				{
					passData(UploadImageFileFxController.class, "uploadedImage",
					         ConfirmImageFxController.class, "faceImage");
				}
				else if(Source.CAMERA.equals(imageSource))
				{
					passData(FaceCapturingFxController.class, ConfirmImageFxController.class,
					         "faceImage");
				}
				
				renderUiAndWaitForUserInput(ConfirmImageFxController.class);
				break;
			}
			case 3:
			{
				renderUiAndWaitForUserInput(SearchFxController.class);
				
				passData(ConfirmImageFxController.class, SearchByFaceImageWorkflowTask.class,
				         "faceImageBase64");
				executeTask(SearchByFaceImageWorkflowTask.class);
				
				break;
			}
			case 4:
			{
				passData(ConfirmImageFxController.class, ShowResultsFxController.class,
				         "finalImage");
				passData(SearchByFaceImageWorkflowTask.class,  ShowResultsFxController.class,
				         "candidates");
				
				renderUiAndWaitForUserInput(ShowResultsFxController.class);
				break;
			}
		}
	}
}