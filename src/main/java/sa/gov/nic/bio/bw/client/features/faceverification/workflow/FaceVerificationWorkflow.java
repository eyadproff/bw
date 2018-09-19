package sa.gov.nic.bio.bw.client.features.faceverification.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.WithLookups;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.faceverification.MatchingFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.ShowResultFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ConfirmImageFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController.Source;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.UploadImageFileFxController;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@WithLookups(CountriesLookup.class)
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.personId"),
		@Step(iconId = "question", title = "wizard.imageSource"),
		@Step(iconId = "upload", title = "wizard.uploadImage"),
		@Step(iconId = "unlock", title = "wizard.confirm"),
		@Step(iconId = "\\uf248", title = "wizard.matching"),
		@Step(iconId = "file_text_alt", title = "wizard.showResult")})
public class FaceVerificationWorkflow extends WizardWorkflowBase
{
	public FaceVerificationWorkflow(AtomicReference<FormRenderer> formRenderer,
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
				renderUi(ImageSourceFxController.class);
				waitForUserInput();
				return true;
			}
			case 2:
			{
				Source imageSource = getData(ImageSourceFxController.class, "imageSource");
				
				if(Source.UPLOAD.equals(imageSource))
				{
					renderUi(UploadImageFileFxController.class);
				}
				else if(Source.CAMERA.equals(imageSource))
				{
					setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);
					renderUi(FaceCapturingFxController.class);
				}
				
				waitForUserInput();
				return true;
			}
			case 3:
			{
				Source imageSource = getData(ImageSourceFxController.class, "imageSource");
				
				passData(PersonIdPaneFxController.class, ConfirmImageFxController.class,
				         "personId");
				
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
			case 4:
			{
				renderUi(MatchingFxController.class);
				waitForUserInput();
				
				passData(PersonIdPaneFxController.class, FaceVerificationWorkflowTask.class,
				         "personId");
				passData(ConfirmImageFxController.class, FaceVerificationWorkflowTask.class,
				         "faceImageBase64");
				executeTask(FaceVerificationWorkflowTask.class);
				
				return true;
			}
			case 5:
			{
				passData(PersonIdPaneFxController.class, ShowResultFxController.class,
				         "personId");
				passData(ConfirmImageFxController.class, ShowResultFxController.class,
				         "faceImage");
				passData(FaceVerificationWorkflowTask.class, ShowResultFxController.class,
				         "faceMatchingResponse");
				
				renderUi(ShowResultFxController.class);
				waitForUserInput();
				return true;
			}
			default: return false;
		}
	}
}