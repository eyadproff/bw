package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import sa.gov.nic.bio.bw.client.core.Context;
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
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryService;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryStatusCheckerService;
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
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.register.registerConvictedPresent", title = "menu.title", order = 2,
				devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA})
@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
@Wizard({@Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "camera", title = "wizard.facePhotoCapturing"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.inquiryResult"),
		@Step(iconId = "user", title = "wizard.personInfo"),
		@Step(iconId = "gavel", title = "wizard.judgementDetails"),
		@Step(iconId = "university", title = "wizard.punishmentDetails"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showReport")})
public class RegisterConvictedReportPresentWorkflow extends WizardWorkflowBase
{
	public RegisterConvictedReportPresentWorkflow(AtomicReference<FormRenderer> formRenderer,
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
				boolean acceptBadQualityFingerprint = "true".equals(Context.getConfigManager().getProperty(
													"printConvictedReport.fingerprint.acceptBadQualityFingerprint"));
				int acceptedBadQualityFingerprintMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
										"printConvictedReport.fingerprint.acceptedBadQualityFingerprintMinRetries"));
		
				uiInputData.put(FingerprintCapturingFxController.KEY_HIDE_FINGERPRINT_PREVIOUS_BUTTON,
				                Boolean.TRUE);
				uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FINGERPRINT,
				                acceptBadQualityFingerprint);
				uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FINGERPRINT_MIN_RETIRES,
								acceptedBadQualityFingerprintMinRetries);
		
				renderUi(FingerprintCapturingFxController.class);
				waitForUserInput();
				return true;
			}
			case 1:
			{
				boolean acceptBadQualityFace = "true".equals(Context.getConfigManager().getProperty(
																"printConvictedReport.face.acceptBadQualityFace"));
				int acceptedBadQualityFaceMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
													"printConvictedReport.face.acceptedBadQualityFaceMinRetries"));
		
				uiInputData.put(FaceCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FACE, acceptBadQualityFace);
				uiInputData.put(FaceCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FACE_MIN_RETIRES,
				                acceptedBadQualityFaceMinRetries);
		
				renderUi(FaceCapturingFxController.class);
				waitForUserInput();
				return true;
			}
			case 2:
			{
				Boolean retry = (Boolean) uiInputData.get(
						InquiryByFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
				uiInputData.remove(InquiryByFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
		
				if(retry == null || !retry)
				{
					// show progress only
					renderUi(InquiryByFingerprintsPaneFxController.class);
					waitForUserInput();
		
					Boolean running = (Boolean)
							uiInputData.get(InquiryByFingerprintsPaneFxController.KEY_DEVICES_RUNNER_IS_RUNNING);
					if(running != null && !running) break;
				}
		
				@SuppressWarnings("unchecked")
				List<Finger> collectedFingerprints = (List<Finger>)
									uiInputData.get(FingerprintCapturingFxController.KEY_SEGMENTED_FINGERPRINTS);
		
				@SuppressWarnings("unchecked") List<Integer> missingFingerprints = (List<Integer>)
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
								renderUi(InquiryByFingerprintsPaneFxController.class);
								waitForUserInput();
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
								renderUi(InquiryByFingerprintsPaneFxController.class);
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
													PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
													criminalHitBioId);
								uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT, personInfo);
								renderUi(InquiryByFingerprintsPaneFxController.class);
							}
							else // report the error
							{
								uiInputData.put(
										InquiryByFingerprintsPaneFxController.KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS,
						                result.getStatus());
								renderUi(InquiryByFingerprintsPaneFxController.class);
							}
		
							waitForUserInput();
							break;
						}
						else // report the error
						{
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
							renderUi(InquiryByFingerprintsPaneFxController.class);
							waitForUserInput();
							break;
						}
					}
				}
				else // report the error
				{
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					renderUi(InquiryByFingerprintsPaneFxController.class);
					waitForUserInput();
				}
				
				return true;
			}
			case 3:
			{
				renderUi(InquiryResultPaneFxController.class);
				waitForUserInput();
				return true;
			}
			case 4:
			{
				renderUi(PersonInfoPaneFxController.class);
				waitForUserInput();
				return true;
			}
			case 5:
			{
				renderUi(JudgmentDetailsPaneFxController.class);
				waitForUserInput();
				return true;
			}
			case 6:
			{
				renderUi(PunishmentDetailsPaneFxController.class);
				waitForUserInput();
				return true;
			}
			case 7:
			{
				renderUi(ReviewAndSubmitPaneFxController.class);
				waitForUserInput();
		
				if(isLeaving()) break;
		
				ConvictedReport convictedReport = (ConvictedReport)
						uiInputData.get(ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
		
				Long generalFileNumber = (Long)
						uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
		
				if(generalFileNumber == null)
				{
					Long samisId = convictedReport.getSubjSamisId();
					Long civilHitBioId = convictedReport.getSubjBioId();
		
					ServiceResponse<Long> serviceResponse =
							GeneratingGeneralFileNumberService.execute(samisId, civilHitBioId);
		
					if(!serviceResponse.isSuccess())
					{
						uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
						break;
					}
		
					generalFileNumber = serviceResponse.getResult();
					uiInputData.put(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
					                generalFileNumber);
				}
		
				convictedReport.setGeneralFileNum(generalFileNumber);
		
				ServiceResponse<ConvictedReportResponse> serviceResponse =
														SubmittingConvictedReportService.execute(convictedReport);
				uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
				
				return true;
			}
			case 8:
			{
				renderUi(ShowReportPaneFxController.class);
				waitForUserInput();
				return true;
			}
			default: return false;
		}
		
		return true;
	}
}