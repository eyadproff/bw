package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import javafx.scene.image.Image;
import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintWsqResponse;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryStatusCheckerService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.PersonInfoLookupService;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.FingerprintsAfterCroppingPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class FingerprintCardIdentificationWorkflow extends WizardWorkflowBase<Void, Void>
{
	public FingerprintCardIdentificationWorkflow(AtomicReference<FormRenderer> formRenderer,
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
			ServiceResponse<Void> serviceResponse = PersonInfoLookupService.execute();
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
				formRenderer.get().renderForm(ScanFingerprintCardPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 1:
			{
				formRenderer.get().renderForm(SpecifyFingerprintCoordinatesPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			case 2:
			{
				formRenderer.get().renderForm(FingerprintsAfterCroppingPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
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
				
				Map<Integer, String> fingerprintImagesMap = new HashMap<>();
				
				Image RightThumbImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 1);
				Image RightIndexImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 2);
				Image RightMiddleImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 3);
				Image RightRingImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 4);
				Image RightLittleImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 5);
				Image LeftThumbImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 6);
				Image LeftIndexImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 7);
				Image LeftMiddleImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 8);
				Image LeftRingImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 9);
				Image LeftLittleImage = (Image) uiInputData.get(
						SpecifyFingerprintCoordinatesPaneFxController.KEY_PREFIX_FINGERPRINT_IMAGE + 10);
				
				try
				{
					if(RightThumbImage != null) fingerprintImagesMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
				                                                            AppUtils.imageToBase64(RightThumbImage));
					if(RightIndexImage != null) fingerprintImagesMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
				                                                            AppUtils.imageToBase64(RightIndexImage));
					if(RightMiddleImage != null) fingerprintImagesMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
				                                                            AppUtils.imageToBase64(RightMiddleImage));
					if(RightRingImage != null) fingerprintImagesMap.put(FingerPosition.RIGHT_RING.getPosition(),
				                                                            AppUtils.imageToBase64(RightRingImage));
					if(RightLittleImage != null) fingerprintImagesMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
				                                                            AppUtils.imageToBase64(RightLittleImage));
					if(LeftThumbImage != null) fingerprintImagesMap.put(FingerPosition.LEFT_THUMB.getPosition(),
				                                                            AppUtils.imageToBase64(LeftThumbImage));
					if(LeftIndexImage != null) fingerprintImagesMap.put(FingerPosition.LEFT_INDEX.getPosition(),
				                                                            AppUtils.imageToBase64(LeftIndexImage));
					if(LeftMiddleImage != null) fingerprintImagesMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
				                                                            AppUtils.imageToBase64(LeftMiddleImage));
					if(LeftRingImage != null) fingerprintImagesMap.put(FingerPosition.LEFT_RING.getPosition(),
				                                                            AppUtils.imageToBase64(LeftRingImage));
					if(LeftLittleImage != null) fingerprintImagesMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
				                                                            AppUtils.imageToBase64(LeftLittleImage));
					
					uiInputData.put(InquiryResultPaneFxController.KEY_FINGERPRINTS_IMAGES, fingerprintImagesMap);
				}
				catch(Exception e)
				{
					String errorCode = FingerprintCardIdentificationErrorCodes.C013_00002.getCode();
					String[] errorDetails = {"failed to convert images to base64 string!"};
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
				
				Future<sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintWsqResponse>>
											 serviceResponseFuture = Context.getBioKitManager()
																			.getFingerprintUtilitiesService()
																			.convertImagesToWsq(fingerprintImagesMap);
				
				sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintWsqResponse> response;
				try
				{
					response = serviceResponseFuture.get();
				}
				catch(Exception e)
				{
					String errorCode = FingerprintCardIdentificationErrorCodes.C013_00003.getCode();
					String[] errorDetails = {"Failed to call the service for converting to WSQ!"};
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
				
				Map<Integer, String> fingerprintWsqMap = new HashMap<>();
				
				if(response.isSuccess())
				{
					ConvertedFingerprintWsqResponse responseResult = response.getResult();
					Map<Integer, String> result = responseResult.getFingerprintWsqMap();
					fingerprintWsqMap.putAll(result);
				}
				else
				{
					String[] errorDetails = {"Failed to convert to WSQ!"};
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
				
				List<Integer> missingFingerprints = new ArrayList<>();
				for(int i = 1; i <= 10; i++) missingFingerprints.add(i);
				fingerprintWsqMap.keySet().forEach(missingFingerprints::remove);
				
				List<Finger> collectedFingerprints = new ArrayList<>();
				for(Entry<Integer, String> entry : fingerprintWsqMap.entrySet())
				{
					collectedFingerprints.add(new Finger(entry.getKey(), entry.getValue(), null));
				}
				
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
								
								long samisId = result.getSamisId();
								long civilBioId = result.getCivilHitBioId();
								long criminalBioId = result.getCrimnalHitBioId();
								PersonInfo personInfo = result.getPersonInfo();
								
								uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_SAMIS_ID, samisId);
								if(civilBioId > 0) uiInputData.put(
											InquiryResultPaneFxController.KEY_INQUIRY_HIT_CIVIL_BIO_ID, civilBioId);
								if(criminalBioId > 0) uiInputData.put(
									InquiryResultPaneFxController.KEY_INQUIRY_HIT_GENERAL_FILE_NUMBER, criminalBioId);
								uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT, personInfo);
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
				formRenderer.get().renderForm(InquiryResultPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			default:
			{
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
		}
		
		return uiOutputData;
	}
}