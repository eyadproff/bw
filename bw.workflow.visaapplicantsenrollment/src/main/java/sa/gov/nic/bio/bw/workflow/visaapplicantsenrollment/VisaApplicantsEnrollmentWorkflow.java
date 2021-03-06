package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.controllers.ApplicantInfoFxController;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.controllers.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.controllers.ShowReceiptFxController;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.lookups.DialingCodesLookup;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.lookups.PassportTypesLookup;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.lookups.VisaTypesLookup;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.tasks.VisaApplicantsWorkflowTask;

@AssociatedMenu(workflowId = 1010, menuId = "menu.register.visaApplicantsEnrollment", menuTitle = "menu.title",
				menuOrder = 6, devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA, Device.PASSPORT_SCANNER})
@WithLookups({CountriesLookup.class, VisaTypesLookup.class, PassportTypesLookup.class, DialingCodesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.applicantInfo"),
		@Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "camera", title = "wizard.facePhotoCapturing"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showReceipt")})
public class VisaApplicantsEnrollmentWorkflow extends WizardWorkflowBase
{
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
				boolean acceptBadQualityFingerprint = "true".equals(Context.getConfigManager().getProperty(
												"visaApplicantsEnrollment.fingerprint.acceptBadQualityFingerprint"));
				int acceptBadQualityFingerprintMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
										"visaApplicantsEnrollment.fingerprint.acceptBadQualityFingerprintMinRetries"));
				
				setData(SlapFingerprintsCapturingFxController.class, "hidePreviousButton", Boolean.FALSE);
				setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
				        acceptBadQualityFingerprint);
				setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
				        acceptBadQualityFingerprintMinRetries);
				
				renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);
				break;
			}
			case 2:
			{
				boolean acceptBadQualityFace = "true".equals(Context.getConfigManager().getProperty(
																"visaApplicantsEnrollment.face.acceptBadQualityFace"));
				int acceptBadQualityFaceMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
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
				         "facePhoto", "facePhotoBase64");
				passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
				         "fingerprintBase64Images", "slapFingerprints", "missingFingerprints");
				
				renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
				
				passData(ReviewAndSubmitPaneFxController.class, VisaApplicantsWorkflowTask.class,
				         "visaApplicantInfo");
				
				executeWorkflowTask(VisaApplicantsWorkflowTask.class);
				
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
				passData(SlapFingerprintsCapturingFxController.class, ShowReceiptFxController.class,
				         "fingerprintBase64Images");
				
				renderUiAndWaitForUserInput(ShowReceiptFxController.class);
				break;
			}
		}
	}
}