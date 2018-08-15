package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.features.commons.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryStatusCheckerService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.PersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterConvictedReportPresentWorkflow extends WizardWorkflowBase<Void, Void>
{
	public RegisterConvictedReportPresentWorkflow(AtomicReference<FormRenderer> formRenderer,
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
			ServiceResponse<Void> serviceResponse = ConvictedReportLookupService.execute();
			if(serviceResponse.isSuccess()) break;
			else uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
		}
	}
	
	@Override
	public Map<String, Object> onStep(int step) throws InterruptedException, Signal
	{
		Map<String, Object> uiOutputData;
		
		switch(step)
		{
			case 0:
			{
				boolean acceptBadQualityFingerprint = "true".equals(System.getProperty(
						"jnlp.bio.bw.printConvictedReport.fingerprint.acceptBadQualityFingerprint"));
				int acceptedBadQualityFingerprintMinRetries = Integer.parseInt(System.getProperty(
						"jnlp.bio.bw.printConvictedReport.fingerprint.acceptedBadQualityFingerprintMinRetries"));
				
				uiInputData.put(FingerprintCapturingFxController.KEY_HIDE_FINGERPRINT_PREVIOUS_BUTTON,
				                Boolean.TRUE);
				uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FINGERPRINT,
				                acceptBadQualityFingerprint);
				uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FINGERPRINT_MIN_RETIRES,
								acceptedBadQualityFingerprintMinRetries);
				
				formRenderer.get().renderForm(FingerprintCapturingFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 1:
			{
				boolean acceptBadQualityFace = "true".equals(System.getProperty(
											"jnlp.bio.bw.printConvictedReport.face.acceptBadQualityFace"));
				int acceptedBadQualityFaceMinRetries = Integer.parseInt(System.getProperty(
											"jnlp.bio.bw.printConvictedReport.face.acceptedBadQualityFaceMinRetries"));
				
				uiInputData.put(FaceCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FACE, acceptBadQualityFace);
				uiInputData.put(FaceCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FACE_MIN_RETIRES,
				                acceptedBadQualityFaceMinRetries);
				
				formRenderer.get().renderForm(FaceCapturingFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 2:
			{
				Boolean retry = (Boolean) uiInputData.get(
						InquiryByFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
				uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
				
				if(retry == null || !retry)
				{
					// show progress only
					formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
					uiOutputData = waitForUserTask();
					uiInputData.putAll(uiOutputData);
					
					Boolean running = (Boolean)
							uiOutputData.get(InquiryByFingerprintsPaneFxController.KEY_DEVICES_RUNNER_IS_RUNNING);
					if(!running) break;
				}
				
				@SuppressWarnings("unchecked")
				List<Finger> collectedFingerprints = (List<Finger>)
									uiInputData.get(FingerprintCapturingFxController.KEY_SEGMENTED_FINGERPRINTS);
				
				@SuppressWarnings("unchecked")
				List<Integer> missingFingerprints = (List<Integer>)
									uiInputData.get(FingerprintCapturingFxController.KEY_MISSING_FINGERPRINTS);
				
				ServiceResponse<Integer> serviceResponse =
						FingerprintInquiryService.execute(collectedFingerprints, missingFingerprints);
				Integer inquiryId = serviceResponse.getResult();
				
				if(serviceResponse.isSuccess() && inquiryId != null)
				{
					while(true)
					{
						ServiceResponse<FingerprintInquiryStatusResult> response =
															FingerprintInquiryStatusCheckerService.execute(inquiryId);
						
						FingerprintInquiryStatusResult result = response.getResult();
						
						if(response.isSuccess() && result != null)
						{
							if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_PENDING)
							{
								uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY,
								                Boolean.TRUE);
								formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
								uiOutputData = waitForUserTask();
								Boolean cancelled = (Boolean) uiOutputData.get(
									InquiryByFingerprintsPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED);
								if(cancelled == null || !cancelled) continue;
								else uiInputData.remove(
									InquiryByFingerprintsPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED);
							}
							else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_NO_HIT)
							{
								uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
								                Boolean.FALSE);
								formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
							}
							else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_HIT)
							{
								uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
								                Boolean.TRUE);
								long criminalHitBioId = result.getCrimnalHitBioId();
								PersonInfo personInfo = result.getPersonInfo();
								
								if(criminalHitBioId > 0) uiInputData.put(
													PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
													criminalHitBioId);
								uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT,
								                personInfo);
								formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
							}
							else // report the error
							{
								uiInputData.put(
										InquiryByFingerprintsPaneFxController.KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS,
						                result.getStatus());
								formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
							}
							
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							break;
						}
						else // report the error
						{
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
							formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							break;
						}
					}
				}
				else // report the error
				{
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
					uiOutputData = waitForUserTask();
					uiInputData.putAll(uiOutputData);
				}
				
				break;
			}
			case 3:
			{
				formRenderer.get().renderForm(InquiryResultPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 4:
			{
				formRenderer.get().renderForm(PersonInfoPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 5:
			{
				formRenderer.get().renderForm(JudgmentDetailsPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 6:
			{
				formRenderer.get().renderForm(PunishmentDetailsPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 7:
			{
				formRenderer.get().renderForm(ReviewAndSubmitPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				
				while(true)
				{
					ConvictedReport convictedReport = (ConvictedReport)
										uiInputData.get(ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
					if(convictedReport == null) break;
					
					Long generalFileNumber = (Long)
									uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
					
					if(generalFileNumber == null)
					{
						ServiceResponse<Long> serviceResponse = GeneratingGeneralFileNumberService.execute();
						generalFileNumber = serviceResponse.getResult();
						uiInputData.put(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
						                generalFileNumber);
					}
					
					convictedReport.setGeneralFileNum(generalFileNumber);
					
					ServiceResponse<ConvictedReportResponse> serviceResponse =
															SubmittingConvictedReportService.execute(convictedReport);
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
						uiInputData.remove(ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
					}
				}
				
				break;
			}
			case 8:
			{
				formRenderer.get().renderForm(ShowReportPaneFxController.class, uiInputData);
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