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
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.*;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.*;
import sa.gov.nic.bio.bw.workflow.irisinquiry.controllers.InquiryByIrisPaneFxController;
import sa.gov.nic.bio.bw.workflow.irisinquiry.controllers.InquiryByIrisResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.irisinquiry.tasks.IrisInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.irisinquiry.tasks.IrisInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.*;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController.Source;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.tasks.SearchByFacePhotoWorkflowTask;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@AssociatedMenu(workflowId = 1029, menuId = "menu.query.biometricsInquiry", menuTitle = "menu.title",
                menuOrder = 12,
                devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA, Device.IRIS_SCANNER})
@WithLookups({CountriesLookup.class, PersonTypesLookup.class, DocumentTypesLookup.class})
@Wizard({@Step(iconId = "question", title = "wizard.selectInquiryMethod"),
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
                InquiryMethod inquiryMethod = getData(inquiryMethodSelectionFxController.class,
                        "inquiryMethod");
                if (inquiryMethod == InquiryMethod.FACE_PHOTO) {
                    renderUiAndWaitForUserInput(ImageSourceFxController.class);
                }
                else if (inquiryMethod == InquiryMethod.FINGERPRINT) {
                    incrementNSteps(1); // to skip step # 2

                    setData(SlapFingerprintsCapturingFxController.class, "allow9MissingWithNoRole", Boolean.TRUE);
                    setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
                            Boolean.TRUE);
                    setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
                            0);
                    renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);
                }
                else {
                    incrementNSteps(2); // to skip step #2,#3
                    setData(IrisCapturingFxController.class, "hideStartOverButton", Boolean.TRUE);

                    renderUiAndWaitForUserInput(IrisCapturingFxController.class);
                }
                break;
            }
            case 2: {
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
            case 3: {
                Source imageSource = getData(ImageSourceFxController.class, "imageSource");

                InquiryMethod inquiryMethod = getData(inquiryMethodSelectionFxController.class,
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
                    setData(ShowingFingerprintsPaneFxController.class, "hideGenerateNistFileButton", true);
                    renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
                }
                break;
            }
            case 4: {
                InquiryMethod inquiryMethod = getData(inquiryMethodSelectionFxController.class,
                        "inquiryMethod");

                if (InquiryMethod.FINGERPRINT.equals(inquiryMethod)) {

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
                                    if (sCivilPersonId.length() == 10 && sCivilPersonId.startsWith("9")) {
                                        setData(GetDeporteeInfoByIdWorkflowTask.class, "deporteeId",
                                                civilPersonId);
                                        setData(GetDeporteeInfoByIdWorkflowTask.class,
                                                "returnNullResultInCaseNotFound", Boolean.TRUE);
                                        executeWorkflowTask(GetDeporteeInfoByIdWorkflowTask.class);
                                        DeporteeInfo deporteeInfo = getData(GetDeporteeInfoByIdWorkflowTask.class,
                                                "deporteeInfo");
                                        personInfo = new DeporteeInfoToPersonInfoConverter().convert(deporteeInfo);
                                    }
                                    else {
                                        setData(GetPersonInfoByIdWorkflowTask.class, "personId", civilPersonId);
                                        setData(GetPersonInfoByIdWorkflowTask.class,
                                                "returnNullResultInCaseNotFound", Boolean.TRUE);
                                        executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
                                        personInfo = getData(GetPersonInfoByIdWorkflowTask.class, "personInfo");
                                    }

                                    civilPersonInfoMap.put(civilPersonId, personInfo);
                                }

                                setData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, civilPersonInfoMap);
                            }
                        }
                    }

                }
                else if (InquiryMethod.FACE_PHOTO.equals(inquiryMethod)) {
                    renderUiAndWaitForUserInput(SearchFxController.class);
                    passData(ConfirmImageFxController.class, SearchByFacePhotoWorkflowTask.class,
                            "facePhotoBase64");
                    executeWorkflowTask(SearchByFacePhotoWorkflowTask.class);

                }
                else {
                    incrementNSteps(-2);// to skip step #2,#3 on previous
                    passData(IrisInquiryStatusCheckerWorkflowTask.class, InquiryByIrisPaneFxController.class,
                            "status");

                    renderUiAndWaitForUserInput(InquiryByIrisPaneFxController.class);

                    Long tcn = getData(IrisInquiryWorkflowTask.class, "tcn");

                    if (tcn == null) {
                        passData(IrisCapturingFxController.class, "capturedRightIrisCompressedBase64",
                                IrisInquiryWorkflowTask.class, "rightIrisBase64");
                        passData(IrisCapturingFxController.class, "capturedLeftIrisCompressedBase64",
                                IrisInquiryWorkflowTask.class, "leftIrisBase64");

                        executeWorkflowTask(IrisInquiryWorkflowTask.class);
                    }

                    passData(IrisInquiryWorkflowTask.class, IrisInquiryStatusCheckerWorkflowTask.class,
                            "tcn");
                    executeWorkflowTask(IrisInquiryStatusCheckerWorkflowTask.class);

                    IrisInquiryStatusCheckerWorkflowTask.Status
                            status = getData(IrisInquiryStatusCheckerWorkflowTask.class, "status");
                    if (status == IrisInquiryStatusCheckerWorkflowTask.Status.HIT) {
                        Long civilBiometricsId = getData(IrisInquiryStatusCheckerWorkflowTask.class,
                                "civilBiometricsId");
                        if (civilBiometricsId != null) {
                            setData(getClass(), FIELD_CIVIL_HIT, Boolean.TRUE);
                            List<Long> civilPersonIds = getData(IrisInquiryStatusCheckerWorkflowTask.class,
                                    "civilPersonIds");
                            if (!civilPersonIds.isEmpty()) {
                                // LinkedHashMap is ordered
                                Map<Long, PersonInfo> civilPersonInfoMap = new LinkedHashMap<>();

                                for (Long civilPersonId : civilPersonIds) {
                                    if (civilPersonId == null) { continue; }

                                    PersonInfo personInfo;

                                    String sCivilPersonId = String.valueOf(civilPersonId);
                                    if (sCivilPersonId.length() == 10 && sCivilPersonId.startsWith("9")) {
                                        setData(GetDeporteeInfoByIdWorkflowTask.class, "deporteeId",
                                                civilPersonId);
                                        setData(GetDeporteeInfoByIdWorkflowTask.class,
                                                "returnNullResultInCaseNotFound", Boolean.TRUE);
                                        executeWorkflowTask(GetDeporteeInfoByIdWorkflowTask.class);
                                        DeporteeInfo deporteeInfo = getData(GetDeporteeInfoByIdWorkflowTask.class,
                                                "deporteeInfo");
                                        personInfo = new DeporteeInfoToPersonInfoConverter().convert(deporteeInfo);
                                    }
                                    else {
                                        setData(GetPersonInfoByIdWorkflowTask.class, "personId", civilPersonId);
                                        setData(GetPersonInfoByIdWorkflowTask.class,
                                                "returnNullResultInCaseNotFound", Boolean.TRUE);
                                        executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
                                        personInfo = getData(GetPersonInfoByIdWorkflowTask.class, "personInfo");
                                    }

                                    civilPersonInfoMap.put(civilPersonId, personInfo);
                                }

                                setData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, civilPersonInfoMap);
                            }
                        }
                    }
                }
                break;
            }
            case 5: {
                InquiryMethod inquiryMethod = getData(inquiryMethodSelectionFxController.class,
                        "inquiryMethod");

                if (InquiryMethod.FINGERPRINT.equals(inquiryMethod)) {

                    passData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, InquiryByFingerprintsResultPaneFxController.class,
                            "civilPersonInfoMap");
                    passData(FingerprintInquiryWorkflowTask.class, InquiryByFingerprintsResultPaneFxController.class,
                            "inquiryId");
                    setData(InquiryByFingerprintsResultPaneFxController.class, "hideRegisterUnknownButton",
                            Boolean.TRUE);
                    setData(InquiryByFingerprintsResultPaneFxController.class, "hideConfirmationButton",
                            Boolean.TRUE);
                    setData(InquiryByFingerprintsResultPaneFxController.class, "ignoreCriminalFingerprintsInquiryResult",
                            Boolean.TRUE);
                    passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                            InquiryByFingerprintsResultPaneFxController.class, "status", "civilBiometricsId");
                    passData(SlapFingerprintsCapturingFxController.class, InquiryByFingerprintsResultPaneFxController.class,
                            "fingerprintBase64Images");
                    renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);

                }
                else if (InquiryMethod.FACE_PHOTO.equals(inquiryMethod)) {

                    passData(ConfirmImageFxController.class, ShowResultsFxController.class,
                            "facePhotoBase64");
                    passData(SearchByFacePhotoWorkflowTask.class, ShowResultsFxController.class,
                            "candidates");
                    setData(ShowResultsFxController.class, "showReportWithAlahwalLogo", true);
                    renderUiAndWaitForUserInput(
                            sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ShowResultsFxController.class);
                }
                else {
                    passData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, InquiryByIrisResultPaneFxController.class,
                            "civilPersonInfoMap");
                    passData(IrisInquiryStatusCheckerWorkflowTask.class,
                            InquiryByIrisResultPaneFxController.class,
                            "status", "civilBiometricsId");
                    renderUiAndWaitForUserInput(InquiryByIrisResultPaneFxController.class);
                }


                break;
            }
            default:
                break;

        }
    }
}


