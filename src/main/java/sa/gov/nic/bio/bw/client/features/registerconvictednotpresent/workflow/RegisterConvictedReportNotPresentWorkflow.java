package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.workflow;

import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FetchingFingerprintsService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.GetFingerprintAvailabilityService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.GetPersonInfoByIdService;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.FetchingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.utils.RegisterConvictedNotPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.PersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportLookupService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportResponse;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryStatusCheckerService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.GeneratingGeneralFileNumberService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.SubmittingConvictedReportService;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterConvictedReportNotPresentWorkflow extends WizardWorkflowBase<Void, Void>
{
	public RegisterConvictedReportNotPresentWorkflow(AtomicReference<FormRenderer> formRenderer,
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
		Map<String, Object> uiOutputData = null;
		
		switch(step)
		{
			case 0:
			{
				formRenderer.get().renderForm(PersonIdPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				
				while(true)
				{
					Long personId = (Long) uiOutputData.get(PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
					
					ServiceResponse<PersonInfo> serviceResponse = GetPersonInfoByIdService.execute(personId,
					                                                                               0);
					
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					formRenderer.get().renderForm(PersonIdPaneFxController.class, uiInputData);
					uiOutputData = waitForUserTask();
					uiInputData.putAll(uiOutputData);
					
					PersonInfo personInfo = serviceResponse.getResult();
					if(serviceResponse.isSuccess() && personInfo != null)
					{
						uiInputData.put(FaceCapturingFxController.KEY_FINAL_FACE_IMAGE, personInfo.getFace());
						break;
					}
				}
				
				break;
			}
			case 1:
			{
				formRenderer.get().renderForm(InquiryResultPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 2:
			{
				formRenderer.get().renderForm(FetchingFingerprintsPaneFxController.class, uiInputData);
				
				while(true)
				{
					
					Long personId = (Long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
					
					ServiceResponse<List<Finger>> serviceResponse = FetchingFingerprintsService.execute(personId);
					
					uiInputData.remove(FetchingFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_FETCHING);
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					formRenderer.get().renderForm(FetchingFingerprintsPaneFxController.class, uiInputData);
					uiOutputData = waitForUserTask();
					uiInputData.putAll(uiOutputData);
					
					Boolean retry = (Boolean) uiInputData.get(
												FetchingFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_FETCHING);
					
					if(retry == null || !retry) break;
				}
				
				break;
			}
			case 3:
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
					
					Boolean running = (Boolean) uiOutputData.get(
												InquiryByFingerprintsPaneFxController.KEY_DEVICES_RUNNER_IS_RUNNING);
					if(running != null && !running) break;
				}
				
				@SuppressWarnings("unchecked")
				List<Finger> collectedFingerprints = (List<Finger>)
										uiInputData.get(FingerprintCapturingFxController.KEY_SLAP_FINGERPRINTS);
				
				Long personId = (Long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
				ServiceResponse<List<Integer>> sr = GetFingerprintAvailabilityService.execute(personId);
				List<Integer> availableFingerprints = sr.getResult();
				
				if(!sr.isSuccess() || availableFingerprints == null)
				{
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, sr);
					formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
					uiOutputData = waitForUserTask();
					uiInputData.putAll(uiOutputData);
					break;
				}
				
				List<Integer> missingFingerprints = new ArrayList<>();
				for(int i = 1; i <= 10; i++) missingFingerprints.add(i);
				availableFingerprints.forEach(missingFingerprints::remove);
				uiInputData.put(FingerprintCapturingFxController.KEY_MISSING_FINGERPRINTS,
				                missingFingerprints);
				
				Map<Integer, String> fingerprintWsqMap = new HashMap<>();
				Map<Integer, String> fingerprintImages = new HashMap<>();
				
				for(Finger finger : collectedFingerprints)
				{
					int position = finger.getType();
					
					if(position == FingerPosition.RIGHT_SLAP.getPosition() ||
							position == FingerPosition.LEFT_SLAP.getPosition() ||
							position == FingerPosition.TWO_THUMBS.getPosition())
					{
						String slapImageBase64 = finger.getImage();
						String slapImageFormat = "WSQ";
						int expectedFingersCount = 0;
						List<Integer> slapMissingFingers = new ArrayList<>();
						
						if(position == FingerPosition.RIGHT_SLAP.getPosition())
						{
							for(int i = FingerPosition.RIGHT_INDEX.getPosition();
							    i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++)
							{
								if(availableFingerprints.contains(i)) expectedFingersCount++;
								else slapMissingFingers.add(i);
							}
						}
						else if(position == FingerPosition.LEFT_SLAP.getPosition())
						{
							for(int i = FingerPosition.LEFT_INDEX.getPosition();
							    i <= FingerPosition.LEFT_LITTLE.getPosition(); i++)
							{
								if(availableFingerprints.contains(i)) expectedFingersCount++;
								else slapMissingFingers.add(i);
							}
						}
						else if(position == FingerPosition.TWO_THUMBS.getPosition())
						{
							if(availableFingerprints.contains(FingerPosition.RIGHT_THUMB.getPosition()))
								expectedFingersCount++;
							else slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition());
							
							if(availableFingerprints.contains(FingerPosition.LEFT_THUMB.getPosition()))
								expectedFingersCount++;
							else slapMissingFingers.add(FingerPosition.LEFT_THUMB.getPosition());
						}
						
						Future<sa.gov.nic.bio.biokit.beans.ServiceResponse<SegmentFingerprintsResponse>>
								serviceResponseFuture = Context.getBioKitManager()
															   .getFingerprintUtilitiesService()
															   .segmentSlap(slapImageBase64, slapImageFormat, position,
								                                            expectedFingersCount, slapMissingFingers);
						
						sa.gov.nic.bio.biokit.beans.ServiceResponse<SegmentFingerprintsResponse> response;
						try
						{
							response = serviceResponseFuture.get();
						}
						catch(Exception e)
						{
							String errorCode = RegisterConvictedNotPresentErrorCodes.C009_00003.getCode();
							String[] errorDetails = {"Failed to call the service for segmenting the fingerprints!"};
							uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE, errorCode);
							uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION, e);
							uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS,
							                errorDetails);
							formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							break;
						}
						
						if(response.isSuccess())
						{
							SegmentFingerprintsResponse result = response.getResult();
							List<DMFingerData> fingerData = result.getFingerData();
							fingerData.forEach(dmFingerData -> fingerprintImages.put(dmFingerData.getPosition(),
							                                                         dmFingerData.getFinger()));
						}
						else
						{
							String[] errorDetails = {"Failed to segment the fingerprints!"};
							uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE,
							                response.getErrorCode());
							uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION,
							                response.getException());
							uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS,
							                errorDetails);
							formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							break;
						}
					}
					else
					{
						if(position == FingerPosition.RIGHT_THUMB_SLAP.getPosition())
																	position = FingerPosition.RIGHT_THUMB.getPosition();
						else if(position == FingerPosition.LEFT_THUMB_SLAP.getPosition())
																	position = FingerPosition.LEFT_THUMB.getPosition();
						fingerprintWsqMap.put(position, finger.getImage());
					}
				}
				
				if(!fingerprintWsqMap.isEmpty())
				{
					Future<sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintImagesResponse>>
							serviceResponseFuture = Context.getBioKitManager()
							.getFingerprintUtilitiesService()
							.convertWsqToImages(fingerprintWsqMap);
					
					sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintImagesResponse> response;
					try
					{
						response = serviceResponseFuture.get();
					}
					catch(Exception e)
					{
						String errorCode = RegisterConvictedNotPresentErrorCodes.C009_00004.getCode();
						String[] errorDetails = {"Failed to call the service for converting the WSQ!"};
						uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE, errorCode);
						uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION, e);
						uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS, errorDetails);
						formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					
					if(response.isSuccess())
					{
						ConvertedFingerprintImagesResponse responseResult = response.getResult();
						Map<Integer, String> result = responseResult.getFingerprintImagesMap();
						fingerprintImages.putAll(result);
					}
					else
					{
						String[] errorDetails = {"Failed to convert the WSQ!"};
						uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE,
						                response.getErrorCode());
						uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION,
						                response.getException());
						uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS, errorDetails);
						formRenderer.get().renderForm(InquiryByFingerprintsPaneFxController.class, uiInputData);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
				}
				
				uiInputData.put(FingerprintCapturingFxController.KEY_FINGERPRINTS_IMAGES,
				                fingerprintImages);
				
				ServiceResponse<Integer> serviceResponse =
						FingerprintInquiryService.execute(collectedFingerprints, missingFingerprints);
				Integer inquiryId = serviceResponse.getResult();
				
				if(serviceResponse.isSuccess() && inquiryId != null)
				{
					while(true)
					{
						ServiceResponse<FingerprintInquiryStatusResult> response2 =
								FingerprintInquiryStatusCheckerService.execute(inquiryId);
						
						FingerprintInquiryStatusResult result = response2.getResult();
						
						if(response2.isSuccess() && result != null)
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
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, response2);
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