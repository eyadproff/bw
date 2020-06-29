package sa.gov.nic.bio.bw.workflow.citizenenrollment;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers.*;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.*;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AssociatedMenu(workflowId = 1002, menuId = "menu.register.citizenEnrollment", menuTitle = "menu.title", menuOrder = 1,
        devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA, Device.IRIS_SCANNER})
@WithLookups({PersonTypesLookup.class, CountriesLookup.class, DocumentTypesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
        @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
        @Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
        @Step(iconId = "camera", title = "wizard.facePhotoCapturing"),
        @Step(iconId = "eye", title = "wizard.irisCapturing"),
        @Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
        @Step(iconId = "save", title = "wizard.enrollmentPerson")})
public class CitizenEnrollmentWorkflow extends WizardWorkflowBase {
    @Override
    public void onStep(int step) throws InterruptedException, Signal {
        switch (step) {
            case 0: {
                renderUiAndWaitForUserInput(PersonIdPaneFxController.class);


                passData(PersonIdPaneFxController.class, GetPersonInfoByIdWorkflowTask.class,
                        "personId");
//              setData(GetPersonInfoByIdWorkflowTask.class, "returnNullResultInCaseNotFound", true);
                executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);


                //check enroll or not
                passData(GetPersonInfoByIdWorkflowTask.class, IsEnrolledWorkflowTask.class,
                        "personInfo");
                executeWorkflowTask(IsEnrolledWorkflowTask.class);

                //death
                passData(GetPersonInfoByIdWorkflowTask.class, DeathIndicatorWorkflowTask.class,
                        "personInfo");
                executeWorkflowTask(DeathIndicatorWorkflowTask.class);

                //different gender
                passData(GetPersonInfoByIdWorkflowTask.class, IsSameGenderWorkflowTask.class,
                        "personInfo");
                executeWorkflowTask(IsSameGenderWorkflowTask.class);


                break;
            }
            case 1: {
                passData(PersonIdPaneFxController.class, ShowingPersonInfoFxController.class,
                        "personInfo");
                renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);

//
                //   renderUiAndWaitForUserInput(IrisCapturingFxController.class);
//                renderUiAndWaitForUserInput(RegisteringCitizenPaneFxController.class);

                setData(RetrieveBioExclusionsWorkflowTask.class, "samisId",
                        (((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class, "normalizedPersonInfo")).getPersonId()).intValue());
                executeWorkflowTask(RetrieveBioExclusionsWorkflowTask.class);


//                setData(RegisteringCitizenPaneFxController.class,"skipIris",false);
//                renderUiAndWaitForUserInput(RegisteringCitizenPaneFxController.class);
//
//                RegisteringCitizenPaneFxController.Request request = getData(RegisteringCitizenPaneFxController.class, "request");
//                if(request == RegisteringCitizenPaneFxController.Request.SUBMIT_CITIZEN_REGISTRATION)
//                {
//                 //   passData(ReviewAndSubmitPaneFxController.class, CitizenRegistrationWorkflowTask.class, "citizenEnrollmentInfo");
//                    executeWorkflowTask(CitizenRegistrationWorkflowTask.class);
//                }
//                else if(request == RegisteringCitizenPaneFxController.Request.CHECK_CITIZEN_REGISTRATION)
//                {
//                   // setData(CheckCitizenRegistrationWorkflowTask.class,"personId",((NormalizedPersonInfo)getData(ShowingPersonInfoFxController.class,"normalizedPersonInfo")).getPersonId());
//                    executeWorkflowTask(CheckCitizenRegistrationWorkflowTask.class);
//                    passData(CheckCitizenRegistrationWorkflowTask.class, "status",
//                            RegisteringCitizenPaneFxController.class, "citizenRegistrationStatus");
//                }
//                else  if(request == RegisteringCitizenPaneFxController.Request.SUBMIT_IRIS_REGISTRATION)
//                {
//                    System.out.println("test");
//                   // passData(ReviewAndSubmitPaneFxController.class, SubmitIrisRegistrationWorkflowTask.class, "citizenEnrollmentInfo");
//                    executeWorkflowTask(SubmitIrisRegistrationWorkflowTask.class);
//                }
//                else if(request == RegisteringCitizenPaneFxController.Request.CHECK_IRIS_REGISTRATION)
//                {
//                   // passData(SubmitIrisRegistrationWorkflowTask.class,
//                      //      CheckIrisRegistrationWorkflowTask.class, "tcn");
//                    executeWorkflowTask(CheckIrisRegistrationWorkflowTask.class);
//                    passData(CheckIrisRegistrationWorkflowTask.class, "status",
//                            RegisteringCitizenPaneFxController.class, "irisRegistrationStatus");
//                }


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

                List<BioExclusion> bioExclusion = getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusion");
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

                List<BioExclusion> bioExclusion = getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusion");

                for (BioExclusion bioExc : bioExclusion) {
                    if (bioExc.getStatus() == 0 && bioExc.getBioType() == 3 && bioExc.getExpireDate() > Instant.now().getEpochSecond()) {
                        setData(FaceCapturingFxController.class, "acceptBadQualityFace", Boolean.TRUE);
                        setData(FaceCapturingFxController.class, "acceptBadQualityFaceMinRetries",
                                0);
                        break;
                    }
                }

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
                passData(PersonIdPaneFxController.class, ReviewAndSubmitPaneFxController.class, "personInfo");

                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "fingerprintBase64Images");
                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "slapFingerprints");
                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "combinedFingerprints");
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

                break;
            }

            case 6: {

                //submit
                Boolean skipIris = getData(IrisCapturingFxController.class, "Skip");
                setData(RegisteringCitizenPaneFxController.class, "skipIris", skipIris);
                renderUiAndWaitForUserInput(RegisteringCitizenPaneFxController.class);

                RegisteringCitizenPaneFxController.Request request = getData(RegisteringCitizenPaneFxController.class, "request");
                if (request == RegisteringCitizenPaneFxController.Request.SUBMIT_CITIZEN_REGISTRATION) {
                    passData(ReviewAndSubmitPaneFxController.class, CitizenRegistrationWorkflowTask.class, "citizenEnrollmentInfo");
                    executeWorkflowTask(CitizenRegistrationWorkflowTask.class);
                } else if (request == RegisteringCitizenPaneFxController.Request.CHECK_CITIZEN_REGISTRATION) {
                    setData(CheckCitizenRegistrationWorkflowTask.class, "personId", ((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class, "normalizedPersonInfo")).getPersonId());
                    executeWorkflowTask(CheckCitizenRegistrationWorkflowTask.class);
                    passData(CheckCitizenRegistrationWorkflowTask.class, "status",
                            RegisteringCitizenPaneFxController.class, "citizenRegistrationStatus");
                } else if (request == RegisteringCitizenPaneFxController.Request.SUBMIT_IRIS_REGISTRATION) {
                    passData(ReviewAndSubmitPaneFxController.class, SubmitIrisRegistrationWorkflowTask.class, "citizenEnrollmentInfo");
                    executeWorkflowTask(SubmitIrisRegistrationWorkflowTask.class);
                } else if (request == RegisteringCitizenPaneFxController.Request.CHECK_IRIS_REGISTRATION) {
                    passData(SubmitIrisRegistrationWorkflowTask.class,
                            CheckIrisRegistrationWorkflowTask.class, "tcn");
                    executeWorkflowTask(CheckIrisRegistrationWorkflowTask.class);
                    passData(CheckIrisRegistrationWorkflowTask.class, "status",
                            RegisteringCitizenPaneFxController.class, "irisRegistrationStatus");
                }

                break;
            }


            default:
                break;
        }
    }
}