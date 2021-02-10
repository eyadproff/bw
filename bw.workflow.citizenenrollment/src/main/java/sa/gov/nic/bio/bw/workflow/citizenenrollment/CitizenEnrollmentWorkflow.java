package sa.gov.nic.bio.bw.workflow.citizenenrollment;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers.*;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.*;
import sa.gov.nic.bio.bw.workflow.commons.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.IrisCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.RetrieveBioExclusionsWorkflowTask;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AssociatedMenu(workflowId = 1002,
                menuId = "menu.register.citizenEnrollment",
                menuTitle = "menu.title",
                menuOrder = 1,
                devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA, Device.IRIS_SCANNER})
@WithLookups({PersonTypesLookup.class, CountriesLookup.class, DocumentTypesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
                @Step(iconId = "\\uf2b9", title = "wizard" + ".showPersonInformation"),
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

                passData(PersonIdPaneFxController.class, GetPersonInfoByIdWorkflowTask.class, "personId");
                executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);


                //check enroll or not
                passData(PersonIdPaneFxController.class, IsEnrolledWorkflowTask.class, "personId");
                executeWorkflowTask(IsEnrolledWorkflowTask.class);

                //death
                passData(GetPersonInfoByIdWorkflowTask.class, DeathIndicatorWorkflowTask.class, "personInfo");
                executeWorkflowTask(DeathIndicatorWorkflowTask.class);

                //different gender
                passData(GetPersonInfoByIdWorkflowTask.class, IsSameGenderWorkflowTask.class, "personInfo");
                executeWorkflowTask(IsSameGenderWorkflowTask.class);


                break;
            }
            case 1: {
                passData(GetPersonInfoByIdWorkflowTask.class, ShowingPersonInfoFxController.class, "personInfo");
                renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);


                setData(RetrieveBioExclusionsWorkflowTask.class, "samisId",
                        (((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class, "normalizedPersonInfo"))
                                .getPersonId()).intValue());

                executeWorkflowTask(RetrieveBioExclusionsWorkflowTask.class);


                break;
            }
            case 2: {

                List<BioExclusion> bioExclusion = getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusionList");
                List<Integer> exceptionOfFingerprints = new ArrayList<>();
                if (bioExclusion != null) {
                    for (BioExclusion bioExc : bioExclusion) {
                        ///null mean Permanent Exception
                        if (bioExc.getStatus() == 0 && bioExc.getBioType() == 1 && (bioExc.getExpireDate() == null ||
                                                                                    bioExc.getExpireDate() >
                                                                                    Instant.now().getEpochSecond())) {
                            exceptionOfFingerprints.add(bioExc.getPosition());
                        }
                    }
                }

                boolean acceptBadQualityFingerprint = "true".equals(Context.getConfigManager().getProperty(
                        "citizenEnrollment.fingerprint.acceptBadQualityFingerprint"));
                int acceptBadQualityFingerprintMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
                        "citizenEnrollment.fingerprint.acceptBadQualityFingerprintMinRetries"));


                setData(SlapFingerprintsCapturingFxController.class, "exceptionOfFingerprints",
                        exceptionOfFingerprints);
                setData(SlapFingerprintsCapturingFxController.class, "hideCheckBoxOfMissing", Boolean.TRUE);
                //  setData(SlapFingerprintsCapturingFxController.class, "allow9MissingWithNoRole", Boolean.TRUE);

                // user without permission can not accept Bad Quality Fingerprint
                @SuppressWarnings("unchecked")
                List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
                String acceptBadQualityRole = Context.getConfigManager().getProperty("citizenEnrollment.roles.fingerprint.acceptBadQualityFingerprint");
                if (userRoles.contains(acceptBadQualityRole)) {
                    setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
                            acceptBadQualityFingerprintMinRetries);
                    setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
                            acceptBadQualityFingerprint);
                }

                setData(SlapFingerprintsCapturingFxController.class, "hideFingerprintQualityFromTooltip",
                        true);
                setData(SlapFingerprintsCapturingFxController.class, "showPrintAndSaveNumOfTriesReportButton",
                        true);
                setData(SlapFingerprintsCapturingFxController.class, "showPersonInfo",
                        true);

                // We do this because the personInfo Object we used here is different from the Common
                PersonInfo personInfo = getData(GetPersonInfoByIdWorkflowTask.class, "personInfo");
                sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo personInfo1 = new sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo();
                // We just need the name and id so far
                personInfo1.setName(personInfo.getName());
                personInfo1.setSamisId(personInfo.getSamisId());

                setData(SlapFingerprintsCapturingFxController.class, "personInfo", personInfo1);

                renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);


                break;
            }
            case 3: {

                boolean acceptBadQualityFace = "true".equals(Context.getConfigManager().getProperty(
                        "citizenEnrollment.fingerprint.acceptBadQualityFace"));
                int acceptBadQualityFaceMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
                        "citizenEnrollment.fingerprint.acceptBadQualityFaceMinRetries"));


                List<BioExclusion> bioExclusion = getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusionList");
                if (bioExclusion != null) {
                    for (BioExclusion bioExc : bioExclusion) {
                        if (bioExc.getStatus() == 0 && bioExc.getBioType() == 3 &&
                            bioExc.getExpireDate() > Instant.now().getEpochSecond()) {
                            setData(FaceCapturingFxController.class, "acceptBadQualityFace", acceptBadQualityFace);
                            setData(FaceCapturingFxController.class, "acceptBadQualityFaceMinRetries", acceptBadQualityFaceMinRetries);
                            break;
                        }
                    }
                }
                setData(FaceCapturingFxController.class, "isImageForEnrollment", true);
                setData(FaceCapturingFxController.class, "showPersonInfo",
                        true);

                // We do this because the personInfo Object we used here is different from the Common
                PersonInfo personInfo = getData(GetPersonInfoByIdWorkflowTask.class, "personInfo");
                sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo personInfo1 = new sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo();
                // We just need the name and id so far
                personInfo1.setName(personInfo.getName());
                personInfo1.setSamisId(personInfo.getSamisId());

                setData(FaceCapturingFxController.class, "personInfo", personInfo1);

                renderUiAndWaitForUserInput(FaceCapturingFxController.class);


                break;
            }

            case 4: {
                setData(IrisCapturingFxController.class,"showSkipButton", Boolean.TRUE);
                setData(IrisCapturingFxController.class, "hideStartOverButton", Boolean.TRUE);

                IrisCapturingFxController.Request irisCapturingRequest = IrisCapturingFxController.Request.ENROLLMENT;
                setData(IrisCapturingFxController.class, "irisCapturingRequest", irisCapturingRequest);
                setData(IrisCapturingFxController.class, "showPersonInfo",
                        Boolean.TRUE);

                // We do this because the personInfo Object we used here is different from the Common
                PersonInfo personInfo = getData(GetPersonInfoByIdWorkflowTask.class, "personInfo");
                sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo personInfo1 = new sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo();
                // We just need the name and id so far
                personInfo1.setName(personInfo.getName());
                personInfo1.setSamisId(personInfo.getSamisId());

                setData(IrisCapturingFxController.class, "personInfo", personInfo1);
                renderUiAndWaitForUserInput(IrisCapturingFxController.class);

                break;
            }
            case 5: {
                //Review

                List<Integer> missingFingerprints = getData(SlapFingerprintsCapturingFxController.class,
                        "missingFingerprints");
                if (missingFingerprints != null && !missingFingerprints.isEmpty()) {
                    // the last one added Exceptions
                    List<BioExclusion> bioExclusion = getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusionList");
                    bioExclusion.sort((o1, o2) -> o2.getCreateDate().compareTo(o1.getCreateDate()));
                    setData(ReviewAndSubmitPaneFxController.class, "supervisorId", bioExclusion.get(0).getOperatorId());
                }
                passData(ShowingPersonInfoFxController.class, ReviewAndSubmitPaneFxController.class,
                        "normalizedPersonInfo");
                passData(GetPersonInfoByIdWorkflowTask.class, ReviewAndSubmitPaneFxController.class, "personInfo");

                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "fingerprintBase64Images");
                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "slapFingerprints");
                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "combinedFingerprints");
                //change
                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "missingFingerprints");

//                passData(FaceCapturingFxController.class, ReviewAndSubmitPaneFxController.class, "facePhoto");
                passData(FaceCapturingFxController.class, "facePhotoBase64ForEnrollment", ReviewAndSubmitPaneFxController.class, "facePhotoBase64");

                passData(IrisCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "capturedRightIrisBase64");
                passData(IrisCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "capturedLeftIrisBase64");
                passData(IrisCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "capturedRightIrisCompressedBase64");
                passData(IrisCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "capturedLeftIrisCompressedBase64");
                renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);

                //Search for face if there is no fingerprints
                if (missingFingerprints != null && missingFingerprints.size() >= 10) {
                    passData(FaceCapturingFxController.class, SearchByFacePhotoWorkflowTask.class,
                            "facePhotoBase64");
                    executeWorkflowTask(SearchByFacePhotoWorkflowTask.class);
                    passData(SearchByFacePhotoWorkflowTask.class, PossibilityFaceImageExistsWorkflowTask.class, "candidates");
                    executeWorkflowTask(PossibilityFaceImageExistsWorkflowTask.class);
                    passData(PossibilityFaceImageExistsWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
                            "IsPossibleFaceImageExists");
                }
                break;
            }

            case 6: {


                renderUiAndWaitForUserInput(RegisteringCitizenPaneFxController.class);

                RegisteringCitizenPaneFxController.Request request = getData(RegisteringCitizenPaneFxController.class,
                        "request");
                if (request == RegisteringCitizenPaneFxController.Request.SUBMIT_CITIZEN_REGISTRATION) {
                    passData(ReviewAndSubmitPaneFxController.class, CitizenRegistrationWorkflowTask.class,
                            "citizenEnrollmentInfo");
                    executeWorkflowTask(CitizenRegistrationWorkflowTask.class);
                    passData(CitizenRegistrationWorkflowTask.class, RegisteringCitizenPaneFxController.class,
                            "isEnrollmentProcessStart");
                }
                else if (request == RegisteringCitizenPaneFxController.Request.CHECK_CITIZEN_REGISTRATION) {
                    setData(CheckCitizenRegistrationWorkflowTask.class, "personId",
                            ((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class,
                                    "normalizedPersonInfo")).getPersonId());
                    executeWorkflowTask(CheckCitizenRegistrationWorkflowTask.class);
                    passData(CheckCitizenRegistrationWorkflowTask.class, "status",
                            RegisteringCitizenPaneFxController.class, "citizenRegistrationStatus");
                }

                break;

            }


            default:
                break;
        }
    }
}