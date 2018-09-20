package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.Device;
import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.controllers.ConfirmImageFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.controllers.ImageSourceFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.controllers.ImageSourceFxController.Source;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.controllers.SearchFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.controllers.ShowResultsFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.controllers.UploadImageFileFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow.SearchByFaceImageWorkflowTask;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.query.searchByFaceImage", title = "menu.title", order = 1, devices = Device.CAMERA)
@Wizard({@Step(iconId = "question", title = "wizard.imageSource"),
		@Step(iconId = "upload", title = "wizard.uploadImage"),
		@Step(iconId = "unlock", title = "wizard.confirm"),
		@Step(iconId = "search", title = "wizard.search"),
		@Step(iconId = "users", title = "wizard.showResults")})
public class SearchByFaceImageWorkflow extends WizardWorkflowBase
{
	public SearchByFaceImageWorkflow(AtomicReference<FormRenderer> formRenderer,
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
				setData(ImageSourceFxController.class, "hidePreviousButton", true);
				renderUi(ImageSourceFxController.class);
				waitForUserInput();
				return true;
			}
			case 1:
			{
				Source imageSource = getData(ImageSourceFxController.class, "imageSource");
				
				if(Source.CAMERA.equals(imageSource))
				{
					setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);
					renderUi(FaceCapturingFxController.class);
				}
				else if(Source.UPLOAD.equals(imageSource))
				{
					renderUi(UploadImageFileFxController.class);
				}
				
				waitForUserInput();
				return true;
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
				
				renderUi(ConfirmImageFxController.class);
				waitForUserInput();
				return true;
			}
			case 3:
			{
				renderUi(SearchFxController.class);
				waitForUserInput();
				
				passData(ConfirmImageFxController.class, SearchByFaceImageWorkflowTask.class,
				         "faceImageBase64");
				executeTask(SearchByFaceImageWorkflowTask.class);
				
				return true;
			}
			case 4:
			{
				passData(ConfirmImageFxController.class, ShowResultsFxController.class,
				         "finalImage");
				passData(SearchByFaceImageWorkflowTask.class,  ShowResultsFxController.class,
				         "candidates");
				
				renderUi(ShowResultsFxController.class);
				waitForUserInput();
				return true;
			}
			default: return false;
		}
	}
}