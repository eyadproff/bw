package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.workflow;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintsResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.features.commons.workflow.ConvictedReportLookupService;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.FetchingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.utils.RegisterConvictedNotPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.InquiryPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.PersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.FingerprintInquiryService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.FingerprintInquiryStatusCheckerService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.GeneratingGeneralFileNumberService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.SubmittingConvictedReportService;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterConvictedReportNotPresentWorkflow extends WorkflowBase<Void, Void>
{
	public RegisterConvictedReportNotPresentWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                                 BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		String basePackage = getClass().getPackage().getName().replace(".", "/");
		basePackage = basePackage.substring(0, basePackage.lastIndexOf('/'));
		
		Map<String, Object> uiInputData = new HashMap<>();
		ResourceBundle stringsBundle;
		try
		{
			stringsBundle = ResourceBundle.getBundle(basePackage + "/bundles/strings",
			                                         Context.getGuiLanguage().getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			e.printStackTrace();
			return null;
		}
		
		URL wizardFxmlLocation = Thread.currentThread().getContextClassLoader()
				.getResource(basePackage + "/fxml/wizard.fxml");
		FXMLLoader wizardPaneLoader = new FXMLLoader(wizardFxmlLocation, stringsBundle);
		wizardPaneLoader.setClassLoader(Context.getFxClassLoader());
		Context.getCoreFxController().loadWizardBar(wizardPaneLoader);
		
		while(true)
		{
			while(true)
			{
				formRenderer.get().renderForm(LookupFxController.class, uiInputData);
				waitForUserTask();
				ServiceResponse<Void> serviceResponse = ConvictedReportLookupService.execute();
				if(serviceResponse.isSuccess()) break;
				else uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
			}
			
			uiInputData.clear();
			int step = 0;
			
			outer: while(true)
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
							Long personId = (Long) uiOutputData.get(
									PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
							Integer personType = (Integer) uiOutputData.get(
									PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_TYPE);
							
							ServiceResponse<PersonInfo> serviceResponse = GetPersonInfoByIdService.execute(personId,
							                                                                               personType);
							
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							formRenderer.get().renderForm(PersonIdPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							
							PersonInfo personInfo = serviceResponse.getResult();
							if(serviceResponse.isSuccess() && personInfo != null)
							{
								uiInputData.put(PersonInfoPaneFxController.KEY_PERSON_INFO_PHOTO, personInfo.getFace());
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
							
							Long personId = (Long) uiInputData.get(
									PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
							
							ServiceResponse<List<Finger>> serviceResponse =
																		FetchingFingerprintsService.execute(personId);
							
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
								InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
						uiInputData.remove(InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
						
						if(retry == null || !retry)
						{
							// show progress only
							formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							
							Boolean running = (Boolean)
											uiOutputData.get(InquiryPaneFxController.KEY_DEVICES_RUNNER_IS_RUNNING);
							if(!running) break;
						}
						
						@SuppressWarnings("unchecked")
						List<Finger> collectedFingerprints = (List<Finger>)
									uiInputData.get(FetchingFingerprintsPaneFxController.KEY_PERSON_FINGERPRINTS);
						
						Long personId = (Long) uiInputData.get(
														PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
						ServiceResponse<List<Integer>> sr = GetFingerprintAvailabilityService.execute(personId);
						List<Integer> availableFingerprints = sr.getResult();
						
						if(!sr.isSuccess() || availableFingerprints == null)
						{
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, sr);
							formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							break;
						}
						
						List<Integer> missingFingerprints = new ArrayList<>();
						for(int i = 1; i <= 10; i++) missingFingerprints.add(i);
						availableFingerprints.forEach(missingFingerprints::remove);
						uiInputData.put(FetchingFingerprintsPaneFxController.KEY_PERSON_MISSING_FINGERPRINTS,
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
									String[] errorDetails =
														{"Failed to call the service for segmenting the fingerprints!"};
									uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_CODE, errorCode);
									uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION, e);
									uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_DETAILS, errorDetails);
									formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_CODE);
									uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
									uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
									uiOutputData = waitForUserTask();
									uiInputData.putAll(uiOutputData);
									break outer;
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
									uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_CODE,
									                response.getErrorCode());
									uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION,
									                response.getException());
									uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_DETAILS, errorDetails);
									formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_CODE);
									uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
									uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
									uiOutputData = waitForUserTask();
									uiInputData.putAll(uiOutputData);
									break outer;
								}
								
							}
							else
							{
								if(position == 11) position = 1;
								else if(position == 12) position = 6;
								fingerprintWsqMap.put(position, finger.getImage());
							}
						}
						
						if(!fingerprintWsqMap.isEmpty())
						{
							Future<sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintsResponse>>
									serviceResponseFuture = Context.getBioKitManager()
									.getFingerprintUtilitiesService()
									.convertWsqToImages(fingerprintWsqMap);
							
							sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintsResponse> response;
							try
							{
								response = serviceResponseFuture.get();
							}
							catch(Exception e)
							{
								String errorCode = RegisterConvictedNotPresentErrorCodes.C009_00004.getCode();
								String[] errorDetails = {"Failed to call the service for converting the WSQ!"};
								uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_CODE, errorCode);
								uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION, e);
								uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_DETAILS, errorDetails);
								formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
								uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_CODE);
								uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
								uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
								uiOutputData = waitForUserTask();
								uiInputData.putAll(uiOutputData);
								break outer;
							}
							
							if(response.isSuccess())
							{
								ConvertedFingerprintsResponse responseResult = response.getResult();
								Map<Integer, String> result = responseResult.getFingerprintImagesMap();
								fingerprintImages.putAll(result);
							}
							else
							{
								String[] errorDetails = {"Failed to convert the WSQ!"};
								uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_CODE,
								                response.getErrorCode());
								uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION,
								                response.getException());
								uiInputData.put(InquiryPaneFxController.KEY_INQUIRY_ERROR_DETAILS, errorDetails);
								formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
								uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_CODE);
								uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
								uiInputData.remove(InquiryPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
								uiOutputData = waitForUserTask();
								uiInputData.putAll(uiOutputData);
								break outer;
							}
							
						}
						
						uiInputData.put(FetchingFingerprintsPaneFxController.KEY_PERSON_FINGERPRINTS_IMAGES,
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
										uiInputData.put(InquiryPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY,
										                Boolean.TRUE);
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
										uiOutputData = waitForUserTask();
										Boolean cancelled = (Boolean) uiOutputData.get(
												InquiryPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED);
										if(cancelled == null || !cancelled) continue;
										else uiInputData.remove(
												InquiryPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED);
									}
									else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_NO_HIT)
									{
										uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
										                Boolean.FALSE);
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									}
									else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_HIT)
									{
										uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
										                Boolean.TRUE);
										long criminalHitBioId = result.getCrimnalHitBioId();
										PersonInfo personInfo = result.getPersonInfo();
										
										if(criminalHitBioId > 0) uiInputData.put(
													PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
													criminalHitBioId);
										uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT,
										                personInfo);
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									}
									else // report the error
									{
										uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS,
										                result.getStatus());
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									}
									
									uiOutputData = waitForUserTask();
									uiInputData.putAll(uiOutputData);
									break;
								}
								else // report the error
								{
									uiInputData.put(KEY_WEBSERVICE_RESPONSE, response2);
									formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									uiOutputData = waitForUserTask();
									uiInputData.putAll(uiOutputData);
									break;
								}
							}
						}
						else // report the error
						{
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
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
							Long generalFileNumber = (Long)
									uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
							
							if(generalFileNumber == null)
							{
								ServiceResponse<Long> serviceResponse = GeneratingGeneralFileNumberService.execute();
								generalFileNumber = serviceResponse.getResult();
								uiInputData.put(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
								                generalFileNumber);
							}
							
							ConvictedReport convictedReport = (ConvictedReport)
									uiInputData.get(ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
							if(convictedReport == null) break;
							
							convictedReport.setGeneralFileNum(generalFileNumber);
							
							ServiceResponse<Long> serviceResponse =
									SubmittingConvictedReportService.execute(convictedReport);
							
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							formRenderer.get().renderForm(ReviewAndSubmitPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							
							if(serviceResponse.isSuccess() && serviceResponse.getResult() != null) break;
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
				}
				
				if(uiOutputData != null)
				{
					Object direction = uiOutputData.get("direction");
					if("backward".equals(direction))
					{
						Platform.runLater(() -> Context.getCoreFxController().moveWizardBackward());
						step--;
					}
					else if("forward".equals(direction))
					{
						Platform.runLater(() -> Context.getCoreFxController().moveWizardForward());
						step++;
					}
					else if("startOver".equals(direction))
					{
						Platform.runLater(() -> Context.getCoreFxController().moveWizardToTheBeginning());
						uiInputData.clear();
						step = 0;
					}
				}
			}
		}
	}
}