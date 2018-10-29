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
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow.VisaApplicantsWorkflowTask;

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
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(ApplicantInfoFxController.class);
				break;
			}
			case 1:
			{
				boolean acceptBadQualityFingerprint = "true".equals(
						Context.getConfigManager().getProperty(
												"visaApplicantsEnrollment.fingerprint.acceptBadQualityFingerprint"));
				int acceptedBadQualityFingerprintMinRetries = Integer.parseInt(
						Context.getConfigManager().getProperty(
									"visaApplicantsEnrollment.fingerprint.acceptedBadQualityFingerprintMinRetries"));
				
				setData(FingerprintCapturingFxController.class, "hidePreviousButton", Boolean.FALSE);
				setData(FingerprintCapturingFxController.class, "acceptBadQualityFingerprint",
				        acceptBadQualityFingerprint);
				setData(FingerprintCapturingFxController.class, "acceptedBadQualityFingerprintMinRetires",
				        acceptedBadQualityFingerprintMinRetries);
				
				renderUiAndWaitForUserInput(FingerprintCapturingFxController.class);
				break;
			}
			case 2:
			{
				boolean acceptBadQualityFace = "true".equals(
						Context.getConfigManager().getProperty("visaApplicantsEnrollment.face.acceptBadQualityFace"));
				int acceptBadQualityFaceMinRetries = Integer.parseInt(
						Context.getConfigManager().getProperty(
													"visaApplicantsEnrollment.face.acceptBadQualityFaceMinRetries"));
				
				setData(FaceCapturingFxController.class, "acceptBadQualityFace", acceptBadQualityFace);
				setData(FaceCapturingFxController.class, "acceptBadQualityFaceMinRetries",
				        acceptBadQualityFaceMinRetries);
				
				renderUiAndWaitForUserInput(FaceCapturingFxController.class);
				break;
			}
			case 3:
			{
				passData(ApplicantInfoFxController.class, ReviewAndSubmitPaneFxController.class,
				         "firstName", "secondName", "otherName", "familyName", "nationality",
				         "gender", "birthPlace", "birthDate", "birthDateUseHijri", "visaType", "passportNumber",
				         "issueDate", "issueDateUseHijri", "expirationDate", "expirationDateUseHijri",
				         "issuanceCountry", "passportType", "dialingCode", "mobileNumber");
				passData(FaceCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
				         "faceImage", "faceImageBase64");
				passData(FingerprintCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
				         "fingerprintImages", "slapFingerprints", "missingFingerprints");
				
				renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
				
				passData(ReviewAndSubmitPaneFxController.class, VisaApplicantsWorkflowTask.class,
				         "visaApplicantInfo");
				
				executeTask(VisaApplicantsWorkflowTask.class);
				
				passData(VisaApplicantsWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
				         "visaApplicantEnrollmentResponse");
				
				break;
			}
			case 4:
			{
				passData(ReviewAndSubmitPaneFxController.class, ShowReceiptFxController.class,
				         "visaApplicantInfo");
				passData(VisaApplicantsWorkflowTask.class, ShowReceiptFxController.class,
				         "visaApplicantEnrollmentResponse");
				passData(FingerprintCapturingFxController.class, ShowReceiptFxController.class,
				         "fingerprintImages");
				
				renderUiAndWaitForUserInput(ShowReceiptFxController.class);
				break;
			}
		}
	}
}