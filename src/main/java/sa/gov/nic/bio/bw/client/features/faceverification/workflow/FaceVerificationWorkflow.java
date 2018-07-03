package sa.gov.nic.bio.bw.client.features.faceverification.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.ConfirmInputFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.MatchingFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.ShowResultFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceMatchingResponse;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.UploadImageFileFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class FaceVerificationWorkflow extends WizardWorkflowBase<Void, Void>
{
	public FaceVerificationWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Map<String, Object> onStep(int step) throws InterruptedException, Signal
	{
		Map<String, Object> uiOutputData;
		
		switch(step)
		{
			case 0:
			{
				formRenderer.get().renderForm(PersonIdPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 1:
			{
				formRenderer.get().renderForm(ImageSourceFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 2:
			{
				String imageInput = (String) uiInputData.get(ImageSourceFxController.KEY_IMAGE_SOURCE);
				
				if(ImageSourceFxController.VALUE_IMAGE_SOURCE_CAMERA.equals(imageInput))
				{
					uiInputData.put(FaceCapturingFxController.KEY_ACCEPT_ANY_CAPTURED_IMAGE, Boolean.TRUE);
					formRenderer.get().renderForm(FaceCapturingFxController.class, uiInputData);
				}
				else if(ImageSourceFxController.VALUE_IMAGE_SOURCE_UPLOAD.equals(imageInput))
				{
					formRenderer.get().renderForm(UploadImageFileFxController.class, uiInputData);
				}
				
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 3:
			{
				formRenderer.get().renderForm(ConfirmInputFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 4:
			{
				// show progress indicator here
				formRenderer.get().renderForm(MatchingFxController.class, uiInputData);
				
				String imageBase64 = (String) uiInputData.get(ConfirmInputFxController.KEY_FINAL_IMAGE_BASE64);
				long personId = (long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_ID);
				
				ServiceResponse<FaceMatchingResponse> response = FaceVerificationService.execute(personId, imageBase64);
				uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
				
				// if success, ask for goNext() automatically. Otherwise, show failure message and retry button
				formRenderer.get().renderForm(MatchingFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 5:
			{
				formRenderer.get().renderForm(ShowResultFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			default:
			{
				uiOutputData = waitForUserTask();
				break;
			}
		}
		
		return uiOutputData;
	}
}