package sa.gov.nic.bio.bw.client.features.faceverification.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.wizard.WithLookups;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.faceverification.ConfirmInputFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.MatchingFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.faceverification.ShowResultFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.UploadImageFileFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@WithLookups(CountriesLookup.class)
public class FaceVerificationWorkflow extends WizardWorkflowBase<Void, Void>
{
	public FaceVerificationWorkflow(AtomicReference<FormRenderer> formRenderer,
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
				renderUi(PersonIdPaneFxController.class);
				waitForUserInput();
				break;
			}
			case 1:
			{
				renderUi(ImageSourceFxController.class);
				waitForUserInput();
				break;
			}
			case 2:
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
			case 3:
			{
				renderUi(ConfirmInputFxController.class);
				waitForUserInput();
				break;
			}
			case 4:
			{
				// show progress indicator here
				renderUi(MatchingFxController.class);
				
				String imageBase64 = (String) uiInputData.get(ConfirmInputFxController.KEY_FINAL_IMAGE_BASE64);
				long personId = (long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_ID);
				
				ServiceResponse<PersonInfo> response = FaceVerificationService.execute(personId, imageBase64);
				uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
				
				// if success, ask for goNext() automatically. Otherwise, show failure message and retry button
				renderUi(MatchingFxController.class);
				waitForUserInput();
				break;
			}
			case 5:
			{
				renderUi(ShowResultFxController.class);
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