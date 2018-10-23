package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent;

import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.Device;
import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.controllers.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FetchingFingerprintsService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryStatusCheckerService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.GetFingerprintAvailabilityService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.GetPersonInfoByIdService;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.controllers.FetchingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.controllers.FingerprintsSourceFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.controllers.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.utils.RegisterConvictedNotPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers.PersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportResponse;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.GeneratingGeneralFileNumberService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.SubmittingConvictedReportService;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.register.registerConvictedNotPresent", title = "menu.title", order = 3,
				devices = Device.BIO_UTILITIES)
@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
@Wizard({@Step(iconId = "question", title = "wizard.selectFingerprintsSource"),
		@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
		@Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
		@Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.showInquiryResult"),
		@Step(iconId = "user", title = "wizard.updatePersonInformation"),
		@Step(iconId = "gavel", title = "wizard.addJudgementDetails"),
		@Step(iconId = "university", title = "wizard.addPunishmentDetails"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showReport")})
public class RegisterConvictedReportNotPresentWorkflow extends WizardWorkflowBase
{
	public RegisterConvictedReportNotPresentWorkflow(AtomicReference<FormRenderer> formRenderer,
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
				renderUiAndWaitForUserInput(FingerprintsSourceFxController.class);
				break;
			}
			case 1:
			{
				String fingerprintsSource = (String)
											uiInputData.get(FingerprintsSourceFxController.KEY_FINGERPRINTS_SOURCE);
		
				if(FingerprintsSourceFxController.VALUE_FINGERPRINTS_SOURCE_ENTERING_PERSON_ID
												 .equals(fingerprintsSource))
				{
					renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
		
					Long personId = (Long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
		
					TaskResponse<PersonInfo> taskResponse = GetPersonInfoByIdService.execute(personId,
					                                                                         0);
		
					uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
		
					PersonInfo personInfo = taskResponse.getResult();
					if(taskResponse.isSuccess() && personInfo != null)
					{
						uiInputData.put(FaceCapturingFxController.KEY_FINAL_FACE_IMAGE, personInfo.getFace());
					}
		
					break;
				}
				else if(FingerprintsSourceFxController.VALUE_FINGERPRINTS_SOURCE_SCANNING_FINGERPRINTS_CARD
													  .equals(fingerprintsSource))
				{
					renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
				}
				else if(FingerprintsSourceFxController.VALUE_FINGERPRINTS_SOURCE_UPLOADING_NIST_FILE
													  .equals(fingerprintsSource))
				{
					renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
				}
				
				break;
			}
			case 2:
			{
				String fingerprintsSource = (String)
						uiInputData.get(FingerprintsSourceFxController.KEY_FINGERPRINTS_SOURCE);
		
				if(FingerprintsSourceFxController.VALUE_FINGERPRINTS_SOURCE_ENTERING_PERSON_ID
						.equals(fingerprintsSource))
				{
					renderUiAndWaitForUserInput(InquiryResultPaneFxController.class);
		
					Long personId = (Long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
		
					TaskResponse<List<Finger>> taskResponse = FetchingFingerprintsService.execute(personId);
		
					uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
				}
				
				break;
			}
			case 3:
			{
				break;
			}
			case 4:
			{
				renderUiAndWaitForUserInput(FetchingFingerprintsPaneFxController.class);
		
				while(true)
				{
		
					Long personId = (Long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
		
					TaskResponse<List<Finger>> taskResponse = FetchingFingerprintsService.execute(personId);
		
					uiInputData.remove(FetchingFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_FETCHING);
					uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
					renderUiAndWaitForUserInput(FetchingFingerprintsPaneFxController.class);
		
					Boolean retry = (Boolean) uiInputData.get(
												FetchingFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_FETCHING);
		
					if(retry == null || !retry) break;
				}
				
				break;
			}
			case 5:
			{
				Boolean retry = (Boolean) uiInputData.get(
						InquiryByFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
				uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
		
				if(retry == null || !retry)
				{
					// show progress only
					renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
		
					Boolean running = (Boolean) uiInputData.get(
												InquiryByFingerprintsPaneFxController.KEY_DEVICES_RUNNER_IS_RUNNING);
					if(running != null && !running) break;
				}
		
				@SuppressWarnings("unchecked")
				List<Finger> collectedFingerprints = (List<Finger>)
										uiInputData.get(FingerprintCapturingFxController.KEY_SLAP_FINGERPRINTS);
		
				Long personId = (Long) uiInputData.get(PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
				TaskResponse<List<Integer>> sr = GetFingerprintAvailabilityService.execute(personId);
				List<Integer> availableFingerprints = sr.getResult();
		
				if(!sr.isSuccess() || availableFingerprints == null)
				{
					uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, sr);
					renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
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
		
						Future<TaskResponse<SegmentFingerprintsResponse>>
								serviceResponseFuture = Context.getBioKitManager()
															   .getFingerprintUtilitiesService()
															   .segmentSlap(slapImageBase64, slapImageFormat, position,
								                                            expectedFingersCount, slapMissingFingers);
		
						TaskResponse<SegmentFingerprintsResponse> response;
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
							renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
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
							renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
							uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
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
					Future<TaskResponse<ConvertedFingerprintImagesResponse>>
							serviceResponseFuture = Context.getBioKitManager()
							.getFingerprintUtilitiesService()
							.convertWsqToImages(fingerprintWsqMap);
		
					TaskResponse<ConvertedFingerprintImagesResponse> response;
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
						renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
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
						renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_CODE);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_EXCEPTION);
						uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_INQUIRY_ERROR_DETAILS);
						break;
					}
				}
		
				uiInputData.put(FingerprintCapturingFxController.KEY_FINGERPRINTS_IMAGES,
				                fingerprintImages);
		
				TaskResponse<Integer> taskResponse =
						FingerprintInquiryService.execute(collectedFingerprints, missingFingerprints);
				Integer inquiryId = taskResponse.getResult();
		
				if(taskResponse.isSuccess() && inquiryId != null)
				{
					while(true)
					{
						TaskResponse<FingerprintInquiryStatusResult> response2 =
								FingerprintInquiryStatusCheckerService.execute(inquiryId);
		
						FingerprintInquiryStatusResult result = response2.getResult();
		
						if(response2.isSuccess() && result != null)
						{
							if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_PENDING)
							{
								uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY,
								                Boolean.TRUE);
								renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
								Boolean cancelled = (Boolean) uiInputData.get(
									InquiryByFingerprintsPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED);
								if(cancelled == null || !cancelled) continue;
								else uiInputData.remove(
									InquiryByFingerprintsPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED);
							}
							else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_NO_HIT)
							{
								uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
								                Boolean.FALSE);
								renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
							}
							else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_HIT)
							{
								uiInputData.put(InquiryByFingerprintsPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
								                Boolean.TRUE);
		
								long samisId = result.getSamisId();
								long civilHitBioId = result.getCivilHitBioId();
								long criminalHitBioId = result.getCrimnalHitBioId();
								PersonInfo personInfo = result.getPersonInfo();
		
								uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_SAMIS_ID, samisId);
		
								if(civilHitBioId > 0) uiInputData.put(
											PersonInfoPaneFxController.KEY_PERSON_INFO_CIVIL_BIO_ID, civilHitBioId);
		
								if(criminalHitBioId > 0) uiInputData.put(
									PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER, criminalHitBioId);
								uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT,
								                personInfo);
								renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
							}
							else // report the error
							{
								uiInputData.put(
										InquiryByFingerprintsPaneFxController.KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS,
								        result.getStatus());
								renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
							}
							
							break;
						}
						else // report the error
						{
							uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, response2);
							renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
							break;
						}
					}
				}
				else // report the error
				{
					uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
					renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
				}
				
				break;
			}
			case 6:
			{
				renderUiAndWaitForUserInput(PersonInfoPaneFxController.class);
				break;
			}
			case 7:
			{
				renderUiAndWaitForUserInput(JudgmentDetailsPaneFxController.class);
				break;
			}
			case 8:
			{
				renderUiAndWaitForUserInput(PunishmentDetailsPaneFxController.class);
				break;
			}
			case 9:
			{
				renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
		
				ConvictedReport convictedReport = (ConvictedReport)
						uiInputData.get(ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
		
				Long generalFileNumber = (Long)
						uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
		
				if(generalFileNumber == null)
				{
					Long samisId = convictedReport.getSubjSamisId();
					Long civilHitBioId = convictedReport.getSubjBioId();
		
					TaskResponse<Long> taskResponse =
							GeneratingGeneralFileNumberService.execute(samisId, civilHitBioId);
		
					if(!taskResponse.isSuccess())
					{
						uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
						break;
					}
		
					generalFileNumber = taskResponse.getResult();
					uiInputData.put(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
					                generalFileNumber);
				}
		
				convictedReport.setGeneralFileNum(generalFileNumber);
		
				TaskResponse<ConvictedReportResponse> taskResponse =
															SubmittingConvictedReportService.execute(convictedReport);
				uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
				
				break;
			}
			case 10:
			{
				renderUiAndWaitForUserInput(ShowReportPaneFxController.class);
				break;
			}
		}
	}
}