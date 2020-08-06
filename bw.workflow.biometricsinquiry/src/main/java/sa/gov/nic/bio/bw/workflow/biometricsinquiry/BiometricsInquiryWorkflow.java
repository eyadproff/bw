package sa.gov.nic.bio.bw.workflow.biometricsinquiry;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.biometricsinquiry.controllers.inquiryMethodSelectionFxController;
import sa.gov.nic.bio.bw.workflow.biometricsinquiry.controllers.inquiryMethodSelectionFxController.InquiryMethod;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.*;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.*;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.*;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.tasks.SearchByFacePhotoWorkflowTask;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController.Source;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@AssociatedMenu(workflowId = 1029, menuId = "menu.query.biometricsInquiry", menuTitle = "menu.title",
                menuOrder = 12,
                devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA})
@WithLookups({CountriesLookup.class})
@Wizard({@Step(iconId = "question", title = "wizard.selectVerificationMethod"),
                @Step(iconId = "question", title = "wizard.imageSource"),
                @Step(iconId = "upload", title = "wizard.uploadImage"),
                @Step(iconId = "unlock", title = "wizard.confirm"),
                @Step(iconId = "search", title = "wizard.search"),
                @Step(iconId = "file_text_alt", title = "wizard.showResult")})
public class BiometricsInquiryWorkflow extends WizardWorkflowBase {

    private static final String FIELD_CIVIL_HIT = "CIVIL_HIT";
    private static final String FIELD_CIVIL_PERSON_INFO_MAP = "CIVIL_PERSON_INFO_MAP";

    @Override
    public void onStep(int step) throws InterruptedException, Signal {

        switch (step) {
            case 0: {
                renderUiAndWaitForUserInput(inquiryMethodSelectionFxController.class);
                break;
            }
            case 1: {
                InquiryMethod inquiryMethod =
                        (InquiryMethod) getData(inquiryMethodSelectionFxController.class,
                                "inquiryMethod");
                if (inquiryMethod == InquiryMethod.FACE_PHOTO) {
                    renderUiAndWaitForUserInput(ImageSourceFxController.class);
                }
                else if (inquiryMethod == InquiryMethod.FINGERPRINT) {
                    incrementNSteps(1); // to skip step # 2
                    //                    setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprint",
                    //                            Boolean.TRUE);
                    //                    setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
                    //                            0);
                    //                    renderUiAndWaitForUserInput(SingleFingerprintCapturingFxController.class);
                    setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
                            Boolean.TRUE);
                    setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
                            0);
                    renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);
                }
                break;
            }
            case 2: {
                Source imageSource = (Source) getData(ImageSourceFxController.class, "imageSource");
                if (Source.UPLOAD.equals(imageSource)) {
                    renderUiAndWaitForUserInput(UploadImageFileFxController.class);
                }
                else if (Source.CAMERA.equals(imageSource)) {
                    setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);
                    renderUiAndWaitForUserInput(FaceCapturingFxController.class);
                }
                break;
            }
            case 3: {
                Source imageSource = (Source) getData(ImageSourceFxController.class, "imageSource");
                //                passData(PersonIdPaneFxController.class, ConfirmImageFxController.class, "personId");

                InquiryMethod inquiryMethod =
                        (InquiryMethod) getData(inquiryMethodSelectionFxController.class,
                                "inquiryMethod");

                if (inquiryMethod == InquiryMethod.FACE_PHOTO) {
                    if (Source.UPLOAD.equals(imageSource)) {
                        passData(UploadImageFileFxController.class, "uploadedImage", ConfirmImageFxController.class,
                                "facePhoto");
                    }
                    else if (Source.CAMERA.equals(imageSource)) {
                        passData(FaceCapturingFxController.class, ConfirmImageFxController.class, "facePhoto");
                    }

                    renderUiAndWaitForUserInput(ConfirmImageFxController.class);
                }
                else if (inquiryMethod == InquiryMethod.FINGERPRINT) {
                    incrementNSteps(-1);// to skip step # 2 on previous
                    passData(SlapFingerprintsCapturingFxController.class,
                            ShowingFingerprintsPaneFxController.class,
                            "fingerprintBase64Images");
                    setData(ShowingFingerprintsPaneFxController.class,"hideGenerateNistFileButton",true);
                    renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
                }
                break;
            }
            case 4: {
                InquiryMethod inquiryMethod =
                        (InquiryMethod) getData(inquiryMethodSelectionFxController.class,
                                "inquiryMethod");
                //setData(VerificationProgressPaneFxController.class, "identificationMethod", identificationMethod);

                if (InquiryMethod.FINGERPRINT.equals(inquiryMethod)) {

                    //                    FingerPosition selectedFingerprintPosition = (FingerPosition) getData(SingleFingerprintCapturingFxController.class, "selectedFingerprintPosition");
                    //                    passData(PersonIdPaneFxController.class, FingerprintVerificationWorkflowTask.class,
                    //                            "personId");
                    //                    passData(SingleFingerprintCapturingFxController.class, "capturedFingerprintForBackend",
                    //                            FingerprintVerificationWorkflowTask.class, "fingerprint");
                    //                    setData(FingerprintVerificationWorkflowTask.class, "fingerPosition",
                    //                            selectedFingerprintPosition.getPosition());
                    //                    executeWorkflowTask(FingerprintVerificationWorkflowTask.class);
                    //                    MatchingResponse matchingResponse = (MatchingResponse) this
                    //                            .getData(FingerprintVerificationWorkflowTask.class, "matchingResponse");
                    //                    setData(VerificationProgressPaneFxController.class, "matched", matchingResponse.isMatched());
                    passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                            InquiryByFingerprintsPaneFxController.class,
                            "status");
                    renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
                    Integer inquiryId = getData(FingerprintInquiryWorkflowTask.class, "inquiryId");

                    if (inquiryId == null) {
                        passData(SlapFingerprintsCapturingFxController.class, "slapFingerprints",
                                FingerprintInquiryWorkflowTask.class, "fingerprints");
                        passData(ShowingFingerprintsPaneFxController.class, FingerprintInquiryWorkflowTask.class,
                                "missingFingerprints");

                        executeWorkflowTask(FingerprintInquiryWorkflowTask.class);
                    }

                    setData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                            "ignoreCriminalFingerprintsInquiryResult", Boolean.TRUE);
                    passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
                            "inquiryId");
                    executeWorkflowTask(FingerprintInquiryStatusCheckerWorkflowTask.class);

                    FingerprintInquiryStatusCheckerWorkflowTask.Status
                            status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");
                    if (status == FingerprintInquiryStatusCheckerWorkflowTask.Status.HIT) {
                        Long civilBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                                "civilBiometricsId");
                        if (civilBiometricsId != null) {
                            setData(getClass(), FIELD_CIVIL_HIT, Boolean.TRUE);
                            List<Long> civilPersonIds = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                                    "civilPersonIds");
                            if (!civilPersonIds.isEmpty()) {
                                // LinkedHashMap is ordered
                                Map<Long, PersonInfo> civilPersonInfoMap = new LinkedHashMap<>();

                                for (Long civilPersonId : civilPersonIds) {
                                    if (civilPersonId == null) { continue; }

                                    PersonInfo personInfo;

                                    String sCivilPersonId = String.valueOf(civilPersonId);

                                    setData(GetPersonInfoByIdWorkflowTask.class, "personId", civilPersonId);
                                    setData(GetPersonInfoByIdWorkflowTask.class,
                                            "returnNullResultInCaseNotFound", Boolean.TRUE);
                                    executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
                                    personInfo = getData(GetPersonInfoByIdWorkflowTask.class, "personInfo");


                                    civilPersonInfoMap.put(civilPersonId, personInfo);
                                }

                                setData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, civilPersonInfoMap);
                            }
                        }
                    }

                }
                else if (InquiryMethod.FACE_PHOTO.equals(inquiryMethod)) {
                    renderUiAndWaitForUserInput(SearchFxController.class);
                    //                    passData(PersonIdPaneFxController.class, FaceVerificationWorkflowTask.class, "personId");
                    passData(ConfirmImageFxController.class, SearchByFacePhotoWorkflowTask.class,
                            "facePhotoBase64");
                    executeWorkflowTask(SearchByFacePhotoWorkflowTask.class);
                    //                    FaceMatchingResponse faceMatchingResponse =
                    //                            (FaceMatchingResponse) getData(FaceVerificationWorkflowTask.class, "faceMatchingResponse");
                    //                    setData(VerificationProgressPaneFxController.class, "matched",
                    //                            faceMatchingResponse.isMatched());
                }
                break;
            }
            case 5: {
                InquiryMethod inquiryMethod =
                        (InquiryMethod) getData(inquiryMethodSelectionFxController.class,
                                "inquiryMethod");
                //  this.passData(PersonIdPaneFxController.class, ShowResultFxController.class, "personId");
                if (InquiryMethod.FINGERPRINT.equals(inquiryMethod)) {
                    //                    MatchingResponse matchingResponse = (MatchingResponse) getData(FingerprintVerificationWorkflowTask.class, "matchingResponse");
                    //                    setData(ShowResultFxController.class, "facePhoto",
                    //                            AppUtils.imageFromBase64(matchingResponse.getPersonInfo().getFace()));
                    //                    setData(ShowResultFxController.class, "personInfo", matchingResponse.getPersonInfo());
                    passData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, InquiryByFingerprintsResultPaneFxController.class,
                            "civilPersonInfoMap");
                    setData(InquiryByFingerprintsResultPaneFxController.class, "hideRegisterUnknownButton",
                            Boolean.TRUE);
                    setData(InquiryByFingerprintsResultPaneFxController.class, "hideConfirmationButton",
                            Boolean.TRUE);
                    setData(InquiryByFingerprintsResultPaneFxController.class,
                            "ignoreCriminalFingerprintsInquiryResult",
                            Boolean.TRUE);
                    passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                            InquiryByFingerprintsResultPaneFxController.class, "status", "civilBiometricsId");
                    passData(SlapFingerprintsCapturingFxController.class,
                            InquiryByFingerprintsResultPaneFxController.class,
                            "fingerprintBase64Images");
                    renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);
                }
                else if (InquiryMethod.FACE_PHOTO.equals(inquiryMethod)) {
                    //                    passData(ConfirmImageFxController.class, ShowResultFxController.class, "facePhoto");
                    //                    faceMatchingResponse = (FaceMatchingResponse) this
                    //                            .getData(FaceVerificationWorkflowTask.class, "faceMatchingResponse");
                    //                    setData(ShowResultFxController.class, "personInfo", faceMatchingResponse.getPersonInfo());
                    passData(ConfirmImageFxController.class, ShowResultsFxController.class,
                            "facePhotoBase64");
                    passData(SearchByFacePhotoWorkflowTask.class, ShowResultsFxController.class,
                            "candidates");
                    renderUiAndWaitForUserInput(
                            sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ShowResultsFxController.class);
                }


                break;
            }
            default:
                break;

        }
    }
}


