package sa.gov.nic.bio.bw.workflow.biometricsverification;

import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.commons.tasks.RetrieveBioExclusionsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.biometricsverification.beans.MatchingResponse;
import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationMethodSelectionFxController;
import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationMethodSelectionFxController.VerificationMethod;
import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationProgressPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SingleFingerprintCapturingFxController;
import sa.gov.nic.bio.bw.workflow.biometricsverification.tasks.FingerprintVerificationWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.faceverification.beans.FaceMatchingResponse;
import sa.gov.nic.bio.bw.workflow.faceverification.controllers.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.ShowResultFxController;
import sa.gov.nic.bio.bw.workflow.faceverification.tasks.FaceVerificationWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ConfirmImageFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController.Source;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.UploadImageFileFxController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AssociatedMenu(workflowId = 1028, menuId = "menu.query.biometricsVerification", menuTitle = "menu.title", menuOrder = 11,
                devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA, Device.IRIS_SCANNER})
@WithLookups({CountriesLookup.class, PersonTypesLookup.class, DocumentTypesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.personId"),
                @Step(iconId = "question", title = "wizard.selectVerificationMethod"),
                @Step(iconId = "question", title = "wizard.imageSource"),
                @Step(iconId = "upload", title = "wizard.uploadImage"),
                @Step(iconId = "unlock", title = "wizard.confirm"),
                @Step(iconId = "\\uf248", title = "wizard.matching"),
                @Step(iconId = "file_text_alt", title = "wizard.showResult")})
public class BiometricsVerificationWorkflow extends WizardWorkflowBase {
    @Override
    public void onStep(int step) throws InterruptedException, Signal {

        switch (step) {
            case 0: {
                renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
                break;
            }
            case 1: {
                renderUiAndWaitForUserInput(VerificationMethodSelectionFxController.class);
                setData(RetrieveBioExclusionsWorkflowTask.class,"samisId",((Long)getData(PersonIdPaneFxController.class,"personId")).intValue());
                executeWorkflowTask(RetrieveBioExclusionsWorkflowTask.class);
                break;
            }
            case 2: {
                VerificationMethod verificationMethod = getData(VerificationMethodSelectionFxController.class,
                        "verificationMethod");
                if (verificationMethod == VerificationMethod.FACE_PHOTO) {
                    renderUiAndWaitForUserInput(ImageSourceFxController.class);
                }
                else if (verificationMethod == VerificationMethod.FINGERPRINT) {
                    incrementNSteps(2); // to skip steps #3 #4 on going next
                    setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprint",
                            Boolean.TRUE);
                    setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
                            0);

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

                    setData(SingleFingerprintCapturingFxController.class, "exceptionOfFingerprints",
                            exceptionOfFingerprints);

                    renderUiAndWaitForUserInput(SingleFingerprintCapturingFxController.class);
                }


                break;
            }
            case 3: {

                Source imageSource = getData(ImageSourceFxController.class, "imageSource");

                if (Source.UPLOAD.equals(imageSource)) {
                    renderUiAndWaitForUserInput(UploadImageFileFxController.class);
                }
                else if (Source.CAMERA.equals(imageSource)) {
                    setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);
                    renderUiAndWaitForUserInput(FaceCapturingFxController.class);
                }

                break;
            }
            case 4: {

                Source imageSource = getData(ImageSourceFxController.class, "imageSource");

                passData(PersonIdPaneFxController.class, ConfirmImageFxController.class,
                        "personId");

                if (Source.UPLOAD.equals(imageSource)) {
                    passData(UploadImageFileFxController.class, "uploadedImage",
                            ConfirmImageFxController.class, "facePhoto");
                }
                else if (Source.CAMERA.equals(imageSource)) {
                    passData(FaceCapturingFxController.class, ConfirmImageFxController.class,
                            "facePhoto");
                }

                renderUiAndWaitForUserInput(ConfirmImageFxController.class);
                break;
            }
            case 5: {

                VerificationMethod verificationMethod = getData(VerificationMethodSelectionFxController.class,
                        "verificationMethod");

                setData(VerificationProgressPaneFxController.class, "verificationMethod", verificationMethod);
                //solve go previous problem: incrementNSteps before render
                if (VerificationMethod.FINGERPRINT.equals(verificationMethod)) {
                    incrementNSteps(-2); // to skip steps #3 #4 on going previous
                }

                renderUiAndWaitForUserInput(VerificationProgressPaneFxController.class);

                if (VerificationMethod.FINGERPRINT.equals(verificationMethod)) {
//                    incrementNSteps(-2); // to skip steps #3 #4 on going previous

                    FingerPosition selectedFingerprintPosition = getData(SingleFingerprintCapturingFxController.class,
                            "selectedFingerprintPosition");

                    passData(PersonIdPaneFxController.class, FingerprintVerificationWorkflowTask.class,
                            "personId");
                    passData(SingleFingerprintCapturingFxController.class, "capturedFingerprintForBackend",
                            FingerprintVerificationWorkflowTask.class, "fingerprint");
                    setData(FingerprintVerificationWorkflowTask.class, "fingerPosition",
                            selectedFingerprintPosition.getPosition());

                    executeWorkflowTask(FingerprintVerificationWorkflowTask.class);

                    MatchingResponse matchingResponse =
                            getData(FingerprintVerificationWorkflowTask.class, "matchingResponse");

                    setData(VerificationProgressPaneFxController.class,
                            "matched", matchingResponse.isMatched());
                }
                else if (VerificationMethod.FACE_PHOTO.equals(verificationMethod)) {
                    passData(PersonIdPaneFxController.class, FaceVerificationWorkflowTask.class,
                            "personId");
                    passData(ConfirmImageFxController.class, FaceVerificationWorkflowTask.class,
                            "facePhotoBase64");

                    executeWorkflowTask(FaceVerificationWorkflowTask.class);

                    FaceMatchingResponse faceMatchingResponse = getData(FaceVerificationWorkflowTask.class,
                            "faceMatchingResponse");
                    setData(VerificationProgressPaneFxController.class,
                            "matched", faceMatchingResponse.isMatched());
                }

                break;
            }
            case 6: {
                VerificationMethod verificationMethod = getData(VerificationMethodSelectionFxController.class,
                        "verificationMethod");
                setData(ShowResultFxController.class, "verificationMethod", verificationMethod);
                passData(PersonIdPaneFxController.class, ShowResultFxController.class,
                        "personId");

                if (VerificationMethod.FINGERPRINT.equals(verificationMethod)) {

                    MatchingResponse matchingResponse =
                            getData(FingerprintVerificationWorkflowTask.class, "matchingResponse");


                    setData(ShowResultFxController.class, "personInfo",
                            matchingResponse.getPersonInfo());
                }
                else if (VerificationMethod.FACE_PHOTO.equals(verificationMethod)) {

                    passData(ConfirmImageFxController.class, ShowResultFxController.class,
                            "facePhotoBase64");

                    FaceMatchingResponse faceMatchingResponse =
                            getData(FaceVerificationWorkflowTask.class, "faceMatchingResponse");

                    setData(GetPersonInfoByIdWorkflowTask.class,"personId",faceMatchingResponse.getPersonInfo().getSamisId());
                    executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);

                    passData(GetPersonInfoByIdWorkflowTask.class,ShowResultFxController.class, "personInfo");

                }

                renderUiAndWaitForUserInput(ShowResultFxController.class);
                break;
            }
        }

    }
}



