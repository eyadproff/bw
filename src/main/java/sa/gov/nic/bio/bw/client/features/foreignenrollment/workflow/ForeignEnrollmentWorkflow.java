package sa.gov.nic.bio.bw.client.features.foreignenrollment.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.ApplicantInfoFxController;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.LookupFxController;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.ShowReceiptFxController;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.ForeignInfo;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ForeignEnrollmentWorkflow extends WizardWorkflowBase<Void, Void>
{
	public ForeignEnrollmentWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                 BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public void init() throws InterruptedException, Signal
	{
		while(true)
		{
			formRenderer.get().renderForm(LookupFxController.class, uiInputData);
			waitForUserTask();
			ServiceResponse<Void> serviceResponse = LookupService.execute();
			if(serviceResponse.isSuccess()) break;
			else uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
		}
	}
	
	@Override
	public Map<String, Object> onStep(int step) throws InterruptedException, Signal
	{
		Map<String, Object> uiOutputData = null;
		
		switch(step)
		{
			case 0:
			{
				formRenderer.get().renderForm(ApplicantInfoFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 1:
			{
				boolean acceptBadQualityFingerprint = "true".equals(System.getProperty(
						"jnlp.bio.bw.foreignEnrollment.fingerprint.acceptBadQualityFingerprint"));
				int acceptedBadQualityFingerprintMinRetries = Integer.parseInt(System.getProperty(
						"jnlp.bio.bw.foreignEnrollment.fingerprint.acceptedBadQualityFingerprintMinRetries"));
				
				uiInputData.put(FingerprintCapturingFxController.KEY_HIDE_FINGERPRINT_PREVIOUS_BUTTON, Boolean.FALSE);
				uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FINGERPRINT,
				                acceptBadQualityFingerprint);
				uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FINGERPRINT_MIN_RETIRES,
				                acceptedBadQualityFingerprintMinRetries);
				
				formRenderer.get().renderForm(FingerprintCapturingFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 2:
			{
				boolean acceptBadQualityFace = "true".equals(System.getProperty(
						"jnlp.bio.bw.foreignEnrollment.face.acceptBadQualityFace"));
				int acceptedBadQualityFaceMinRetries = Integer.parseInt(System.getProperty(
						"jnlp.bio.bw.foreignEnrollment.face.acceptedBadQualityFaceMinRetries"));
				
				uiInputData.put(FaceCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FACE, acceptBadQualityFace);
				uiInputData.put(FaceCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FACE_MIN_RETIRES,
				                acceptedBadQualityFaceMinRetries);
				
				formRenderer.get().renderForm(FaceCapturingFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 3:
			{
				formRenderer.get().renderForm(ReviewAndSubmitPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				
				while(true)
				{
					ForeignInfo foreignInfo = (ForeignInfo)
													uiInputData.get(ReviewAndSubmitPaneFxController.KEY_FOREIGN_INFO);
					if(foreignInfo == null) break;
					
					ServiceResponse<Long> serviceResponse = ForeignEnrollmentService.execute(foreignInfo);
					boolean success = serviceResponse.isSuccess() && serviceResponse.getResult() != null;
					
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					formRenderer.get().renderForm(ReviewAndSubmitPaneFxController.class, uiInputData);
					
					if(success)
					{
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					else
					{
						uiInputData.remove(ReviewAndSubmitPaneFxController.KEY_FOREIGN_INFO);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
					}
				}
				
				break;
			}
			case 4:
			{
				formRenderer.get().renderForm(ShowReceiptFxController.class, uiInputData);
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