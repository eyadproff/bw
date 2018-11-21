package sa.gov.nic.bio.bw.workflow.searchbyfaceimage;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ConfirmImageFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController.Source;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.SearchFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ShowResultsFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.UploadImageFileFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.tasks.SearchByFacePhotoWorkflowTask;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(workflowId = 1009, menuId = "menu.query.searchByFaceImage", menuTitle = "menu.title", menuOrder = 1, devices = Device.CAMERA)
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
			case 3:
			{
				renderUiAndWaitForUserInput(SearchFxController.class);
				
				passData(ConfirmImageFxController.class, SearchByFacePhotoWorkflowTask.class,
				         "facePhotoBase64");
				executeTask(SearchByFacePhotoWorkflowTask.class);
				
				break;
			}
			case 4:
			{
				passData(ConfirmImageFxController.class, ShowResultsFxController.class,
				         "facePhoto");
				passData(SearchByFacePhotoWorkflowTask.class, ShowResultsFxController.class,
				         "candidates");
				
				renderUiAndWaitForUserInput(ShowResultsFxController.class);
				break;
			}
		}
	}
}