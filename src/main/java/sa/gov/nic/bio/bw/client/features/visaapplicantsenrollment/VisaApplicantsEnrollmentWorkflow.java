package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment;

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
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.controllers.ApplicantInfoFxController;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.controllers.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.controllers.ShowReceiptFxController;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups.DialingCodesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups.PassportTypesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups.VisaTypesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantInfo;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow.VisaApplicantEnrollmentResponse;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow.VisaApplicantsEnrollmentService;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.register.visaApplicantsEnrollment", title = "menu.title", order = 4,
				devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA, Device.PASSPORT_SCANNER})
@WithLookups({CountriesLookup.class, VisaTypesLookup.class, PassportTypesLookup.class, DialingCodesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.applicantInfo"),
		@Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "camera", title = "wizard.facePhotoCapturing"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showReceipt")})
public class VisaApplicantsEnrollmentWorkflow extends WizardWorkflowBase
{
	public VisaApplicantsEnrollmentWorkflow(AtomicReference<FormRenderer> formRenderer,
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
				renderUi(ApplicantInfoFxController.class);
				waitForUserInput();
				return true;
			}
			case 1:
			{
				boolean acceptBadQualityFingerprint = "true".equals(
						Context.getConfigManager().getProperty(
												"visaApplicantsEnrollment.fingerprint.acceptBadQualityFingerprint"));
				int acceptedBadQualityFingerprintMinRetries = Integer.parseInt(
						Context.getConfigManager().getProperty(
									"visaApplicantsEnrollment.fingerprint.acceptedBadQualityFingerprintMinRetries"));
				
				uiInputData.put(FingerprintCapturingFxController.KEY_HIDE_FINGERPRINT_PREVIOUS_BUTTON, Boolean.FALSE);
				uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FINGERPRINT,
				                acceptBadQualityFingerprint);
				uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FINGERPRINT_MIN_RETIRES,
				                acceptedBadQualityFingerprintMinRetries);
				
				renderUi(FingerprintCapturingFxController.class);
				waitForUserInput();
				return true;
			}
			case 2:
			{
				boolean acceptBadQualityFace = "true".equals(
						Context.getConfigManager().getProperty("visaApplicantsEnrollment.face.acceptBadQualityFace"));
				int acceptedBadQualityFaceMinRetries = Integer.parseInt(
						Context.getConfigManager().getProperty(
													"visaApplicantsEnrollment.face.acceptedBadQualityFaceMinRetries"));
				
				uiInputData.put(FaceCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FACE, acceptBadQualityFace);
				uiInputData.put(FaceCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FACE_MIN_RETIRES,
				                acceptedBadQualityFaceMinRetries);
				
				renderUi(FaceCapturingFxController.class);
				waitForUserInput();
				return true;
			}
			case 3:
			{
				renderUi(ReviewAndSubmitPaneFxController.class);
				waitForUserInput();
				
				while(true)
				{
					VisaApplicantInfo visaApplicantInfo = (VisaApplicantInfo)
											uiInputData.get(ReviewAndSubmitPaneFxController.KEY_VISA_APPLICANT_INFO);
					if(visaApplicantInfo == null) break;
					
					ServiceResponse<VisaApplicantEnrollmentResponse> serviceResponse =
															VisaApplicantsEnrollmentService.execute(visaApplicantInfo);
					boolean success = serviceResponse.isSuccess() && serviceResponse.getResult() != null;
					
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					renderUi(ReviewAndSubmitPaneFxController.class);
					
					if(success)
					{
						waitForUserInput();
						break;
					}
					else
					{
						uiInputData.remove(ReviewAndSubmitPaneFxController.KEY_VISA_APPLICANT_INFO);
						waitForUserInput();
					}
				}
				
				return true;
			}
			case 4:
			{
				renderUi(ShowReceiptFxController.class);
				waitForUserInput();
				return true;
			}
			default: return false;
		}
	}
}