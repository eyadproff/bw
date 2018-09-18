package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ConfirmImageFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController.Source;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.SearchFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ShowResultsFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.UploadImageFileFxController;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

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