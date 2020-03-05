package sa.gov.nic.bio.bw.workflow.citizenenrollment;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers.*;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

//import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingPersonInfoFxController;

@AssociatedMenu(workflowId = 1002, menuId = "menu.register.citizenEnrollment", menuTitle = "menu.title", menuOrder = 1,
        devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA, Device.IRIS_SCANNER})
@WithLookups({PersonTypesLookup.class, CountriesLookup.class, DocumentTypesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
        @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
        @Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
        @Step(iconId = "camera", title = "wizard.facePhotoCapturing"),
        @Step(iconId = "eye", title = "wizard.irisCapturing"),
        @Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
        @Step(iconId = "spinner", title = "wizard.enrollment"),
        @Step(iconId = "\\uf0f6", title = "wizard.enrollmentResult")})
public class CitizenEnrollmentWorkflow extends WizardWorkflowBase {
    @Override
    public void onStep(int step) throws InterruptedException, Signal {
        switch (step) {
            case 0: {

                renderUiAndWaitForUserInput(PersonIdPaneFxController.class);

//                passData(PersonIdPaneFxController.class, GetPersonInfoByIdWorkflowTask.class,
//                        "personId");
//
//                  setData(GetPersonInfoByIdWorkflowTask.class, "returnNullResultInCaseNotFound", true);

                //check enroll or not

                //death

                //different gender
//                executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
//                passData(GetPersonInfoByIdWorkflowTask.class, IsSameGenderWorkflowTask.class,
//                        "personInfo");
//
//                executeWorkflowTask(IsSameGenderWorkflowTask.class);


                break;
            }
            case 1: {
                passData(PersonIdPaneFxController.class, ShowingPersonInfoFxController.class,
                        "personInfo");

                renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);

//                setData(RetrieveBioExclusionsWorkflowTask.class, "samisId", (((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class, "normalizedPersonInfo")).getPersonId()).intValue());
//
//                executeWorkflowTask(RetrieveBioExclusionsWorkflowTask.class);
//                List<BioExclusion> bioExclusion = getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusionList");
//                List<Integer> exceptionOfFingerprints = new ArrayList<>();
//                bioExclusion.forEach(bioExc -> {
//                    if (bioExc.getStatus() == 0 && bioExc.getBioType() == 1)
//                        exceptionOfFingerprints.add(bioExc.getPosition());
//                });



                break;
            }
            case 2: {
//missing finger

                //   setData(SlapFingerprintsCapturingFxController.class, "allow9MissingWithNoRole", Boolean.TRUE);


//                boolean acceptBadQualityFingerprint = "true".equals(Context.getConfigManager().getProperty(
//                        "visaApplicantsEnrollment.fingerprint.acceptBadQualityFingerprint"));
//                int acceptBadQualityFingerprintMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
//                        "visaApplicantsEnrollment.fingerprint.acceptBadQualityFingerprintMinRetries"));

//                setData(SlapFingerprintsCapturingFxController.class, "hidePreviousButton", Boolean.FALSE);
//                setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
//                        acceptBadQualityFingerprint);
//                setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
//                        acceptBadQualityFingerprintMinRetries);


//                setData(RetrieveBioExclusionsWorkflowTask.class, "samisId", (((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class, "normalizedPersonInfo")).getPersonId()).intValue());
//
//                executeWorkflowTask(RetrieveBioExclusionsWorkflowTask.class);
//                List<BioExclusion> bioExclusion = getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusionList");
//                List<Integer> exceptionOfFingerprints = new ArrayList<>();
//                bioExclusion.forEach(bioExc -> {
//                    if (bioExc.getStatus() == 0 && bioExc.getBioType() == 1)
//                        exceptionOfFingerprints.add(bioExc.getPosition());
//                });
//                @Input private Boolean hidePreviousButton;
//                @Input private Boolean allow9MissingWithNoRole;
//                @Input private Boolean acceptBadQualityFingerprint;
//                @Input private Integer acceptBadQualityFingerprintMinRetires;
//                @Input private Boolean hideCheckBoxOfMissing;
//                @Input private List<Integer> exceptionOfFingerprints;

                List<BioExclusion> bioExclusion = getData(ShowingPersonInfoFxController.class, "bioExclusion");
                List<Integer> exceptionOfFingerprints = new ArrayList<>();
                if (bioExclusion != null)
                    bioExclusion.forEach(bioExc -> {
                        ///null mean Permanent Exception
                        if (bioExc.getStatus() == 0 && bioExc.getBioType() == 1 && (bioExc.getExpireDate() == null || bioExc.getExpireDate() > Instant.now().getEpochSecond()))
                            exceptionOfFingerprints.add(bioExc.getPosition());
                    });

                setData(SlapFingerprintsCapturingFxController.class, "exceptionOfFingerprints", exceptionOfFingerprints);
                setData(SlapFingerprintsCapturingFxController.class, "hideCheckBoxOfMissing",
                        Boolean.TRUE);
                renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);


                break;
            }
            case 3: {
//face Exception
//                boolean acceptBadQualityFace = "true".equals(Context.getConfigManager().getProperty(
//                        "visaApplicantsEnrollment.face.acceptBadQualityFace"));
//                int acceptBadQualityFaceMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
//                        "visaApplicantsEnrollment.face.acceptBadQualityFaceMinRetries"));
//
//                setData(FaceCapturingFxController.class, "acceptBadQualityFace", acceptBadQualityFace);
//                setData(FaceCapturingFxController.class, "acceptBadQualityFaceMinRetries",
//                        acceptBadQualityFaceMinRetries);
//                setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);

                List<BioExclusion> bioExclusion = getData(ShowingPersonInfoFxController.class, "bioExclusion");

                for (BioExclusion bioExc : bioExclusion) {
                    if (bioExc.getStatus() == 0 && bioExc.getBioType() == 3 && bioExc.getExpireDate() > Instant.now().getEpochSecond()) {
                        setData(FaceCapturingFxController.class, "acceptBadQualityFace", Boolean.TRUE);
                        setData(FaceCapturingFxController.class, "acceptBadQualityFaceMinRetries",
                                0);
                        break;
                    }
                }
//                bioExclusion.forEach(bioExc -> {
//                    if (bioExc.getStatus() == 0 && bioExc.getBioType() == 3) {
//                        setData(FaceCapturingFxController.class, "acceptBadQualityFace", Boolean.TRUE);
//                        setData(FaceCapturingFxController.class, "acceptBadQualityFaceMinRetries",
//                                0);
//                    }
//                });

                renderUiAndWaitForUserInput(FaceCapturingFxController.class);

                break;
            }

            case 4: {
                setData(IrisCapturingFxController.class, "hideStartOverButton", Boolean.TRUE);
                renderUiAndWaitForUserInput(IrisCapturingFxController.class);

                break;
            }
            case 5: {
                //Review

                passData(ShowingPersonInfoFxController.class, ReviewAndSubmitPaneFxController.class, "normalizedPersonInfo");

                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "fingerprintBase64Images");
                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "slapFingerprints");
                //change
                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "missingFingerprints");

                passData(FaceCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "facePhoto");
                passData(FaceCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "facePhotoBase64");

                Boolean SkipIris = getData(IrisCapturingFxController.class, "Skip");
                if (!SkipIris) {
                    passData(IrisCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "capturedRightIrisBase64");
                    passData(IrisCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "capturedLeftIrisBase64");
                }
                renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);

//                passData(ApplicantInfoFxController.class, ReviewAndSubmitPaneFxController.class,
//                        "firstName", "secondName", "otherName", "familyName", "nationality",
//                        "gender", "birthPlace", "birthDate", "birthDateUseHijri", "visaType", "passportNumber",
//                        "issueDate", "issueDateUseHijri", "expirationDate", "expirationDateUseHijri",
//                        "issuanceCountry", "passportType", "dialingCode", "mobileNumber");
//                passData(FaceCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
//                        "facePhoto", "facePhotoBase64");
//                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
//                        "fingerprintBase64Images", "slapFingerprints", "missingFingerprints");
//
//                renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
//
//                passData(ReviewAndSubmitPaneFxController.class, VisaApplicantsWorkflowTask.class,
//                        "visaApplicantInfo");
//
//                executeWorkflowTask(VisaApplicantsWorkflowTask.class);
//
//                passData(VisaApplicantsWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
//                        "visaApplicantEnrollmentResponse");

                //iris
//                passData(PersonIdPaneFxController.class, IrisSumissionWorkflowTask.class,
//                        "personId");
//                passData(IrisCapturingFxController.class, "capturedRightIrisBase64",
//                        IrisSumissionWorkflowTask.class, "rightIrisBase64");
//                passData(IrisCapturingFxController.class, "capturedLeftIrisBase64",
//                        IrisSumissionWorkflowTask.class, "leftIrisBase64");
//                executeWorkflowTask(IrisSumissionWorkflowTask.class);


                //
//                passData(ReviewAndSubmitPaneFxController.class, VisaApplicantsWorkflowTask.class,
//                        "visaApplicantInfo");
//
//                executeWorkflowTask(VisaApplicantsWorkflowTask.class);
//
//                passData(VisaApplicantsWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
//                        "visaApplicantEnrollmentResponse");
                break;
            }
            case 6: {
                //submit
                renderUiAndWaitForUserInput(RegisteringCitizenPaneFxController.class);

//                Request request = getData(RegisteringIrisPaneFxController.class, "request");
//                if(request == Request.SUBMIT_IRIS_REGISTRATION)
//                {
//                    passData(PersonIdPaneFxController.class, SubmitIrisRegistrationWorkflowTask.class,
//                            "personId");
//                    passData(sa.gov.nic.bio.bw.workflow.commons.controllers.IrisCapturingFxController.class, "capturedRightIrisBase64",
//                            SubmitIrisRegistrationWorkflowTask.class, "rightIrisBase64");
//                    passData(sa.gov.nic.bio.bw.workflow.commons.controllers.IrisCapturingFxController.class, "capturedLeftIrisBase64",
//                            SubmitIrisRegistrationWorkflowTask.class, "leftIrisBase64");
//                    executeWorkflowTask(SubmitIrisRegistrationWorkflowTask.class);
//                }
//                else if(request == Request.CHECK_IRIS_REGISTRATION)
//                {
//                    passData(SubmitIrisRegistrationWorkflowTask.class,
//                            CheckIrisRegistrationWorkflowTask.class, "tcn");
//                    executeWorkflowTask(CheckIrisRegistrationWorkflowTask.class);
//                    passData(CheckIrisRegistrationWorkflowTask.class, "status",
//                            RegisteringIrisPaneFxController.class, "irisRegistrationStatus");
//                }

                break;
            }


            default:
                break;
        }
    }
}