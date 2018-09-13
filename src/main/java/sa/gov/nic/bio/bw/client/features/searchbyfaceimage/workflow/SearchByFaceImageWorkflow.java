package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ConfirmImageFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.SearchFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ShowResultsFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.UploadImageFileFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class SearchByFaceImageWorkflow extends WizardWorkflowBase<Void, Void>
{
	public SearchByFaceImageWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                 BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				uiInputData.put(ImageSourceFxController.KEY_HIDE_IMAGE_SOURCE_PREVIOUS_BUTTON, Boolean.TRUE);
				renderUi(ImageSourceFxController.class);
				waitForUserInput();
				break;
			}
			case 1:
			{
				String imageInput = (String) uiInputData.get(ImageSourceFxController.KEY_IMAGE_SOURCE);
				
				if(ImageSourceFxController.VALUE_IMAGE_SOURCE_CAMERA.equals(imageInput))
				{
					uiInputData.put(FaceCapturingFxController.KEY_ACCEPT_ANY_CAPTURED_IMAGE, Boolean.TRUE);
					renderUi(FaceCapturingFxController.class);
				}
				else if(ImageSourceFxController.VALUE_IMAGE_SOURCE_UPLOAD.equals(imageInput))
				{
					renderUi(UploadImageFileFxController.class);
				}
				
				waitForUserInput();
				break;
			}
			case 2:
			{
				renderUi(ConfirmImageFxController.class);
				waitForUserInput();
				break;
			}
			case 3:
			{
				// show progress indicator here
				renderUi(SearchFxController.class);
				
				String imageBase64 = (String) uiInputData.get(ConfirmImageFxController.KEY_FINAL_IMAGE_BASE64);
				ServiceResponse<List<Candidate>> response = SearchByFaceImageService.execute(imageBase64);
				uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
				
				// if success, ask for goNext() automatically. Otherwise, show failure message and retry button
				renderUi(SearchFxController.class);
				waitForUserInput();
				break;
			}
			case 4:
			{
				renderUi(ShowResultsFxController.class);
				waitForUserInput();
				break;
			}
			default:
			{
				waitForUserInput();
				break;
			}
		}
	}
}