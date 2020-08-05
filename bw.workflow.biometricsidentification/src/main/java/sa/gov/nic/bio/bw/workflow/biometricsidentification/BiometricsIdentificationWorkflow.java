package sa.gov.nic.bio.bw.workflow.biometricsidentification;

import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
        import sa.gov.nic.bio.bw.core.utils.AppUtils;
        import sa.gov.nic.bio.bw.core.utils.Device;
        import sa.gov.nic.bio.bw.core.wizard.Step;
        import sa.gov.nic.bio.bw.core.wizard.Wizard;
        import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
        import sa.gov.nic.bio.bw.core.workflow.Signal;
        import sa.gov.nic.bio.bw.core.workflow.WithLookups;
        import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
        import sa.gov.nic.bio.bw.workflow.biometricsverification.beans.MatchingResponse;
        import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.ShowResultFxController;
        import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationMethodSelectionFxController;
        import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationProgressPaneFxController;
        import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationMethodSelectionFxController.VerificationMethod;
        import sa.gov.nic.bio.bw.workflow.biometricsverification.tasks.FingerprintVerificationWorkflowTask;
        import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
        import sa.gov.nic.bio.bw.workflow.commons.controllers.SingleFingerprintCapturingFxController;
        import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
        import sa.gov.nic.bio.bw.workflow.faceverification.beans.FaceMatchingResponse;
        import sa.gov.nic.bio.bw.workflow.faceverification.controllers.PersonIdPaneFxController;
        import sa.gov.nic.bio.bw.workflow.faceverification.tasks.FaceVerificationWorkflowTask;
        import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ConfirmImageFxController;
        import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController;
        import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.UploadImageFileFxController;
        import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController.Source;

@AssociatedMenu(workflowId = 1029, menuId = "menu.query.biometricsIdentification", menuTitle = "menu.title",
                menuOrder = 12,
                devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA})
@WithLookups({CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.personId"),
                @Step(iconId = "question", title = "wizard.selectVerificationMethod"),
                @Step(iconId = "question", title = "wizard.imageSource"),
                @Step(iconId = "upload", title = "wizard.uploadImage"),
                @Step(iconId = "unlock", title = "wizard.confirm"),
                @Step(iconId = "\\uf248", title = "wizard.matching"),
                @Step(iconId = "file_text_alt", title = "wizard.showResult")})
public class BiometricsIdentificationWorkflow extends WizardWorkflowBase {

    @Override
    public void onStep(int step) throws InterruptedException, Signal {

        switch (step) {
            case 0:
                renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
                break;
            case 1:
                renderUiAndWaitForUserInput(VerificationMethodSelectionFxController.class);
                break;
            case 2:
                VerificationMethod verificationMethod =
                        (VerificationMethod) getData(VerificationMethodSelectionFxController.class,
                        "verificationMethod");
                if (verificationMethod == VerificationMethod.FACE_PHOTO) {
                    renderUiAndWaitForUserInput(ImageSourceFxController.class);
                }
                else if (verificationMethod == VerificationMethod.FINGERPRINT) {
                    incrementNSteps(2);
                    setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprint",
                            Boolean.TRUE);
                    setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
                            0);
                    renderUiAndWaitForUserInput(SingleFingerprintCapturingFxController.class);
                }
                break;
            case 3:
                imageSource = (Source) this.getData(ImageSourceFxController.class, "imageSource");
                if (Source.UPLOAD.equals(imageSource)) {
                    renderUiAndWaitForUserInput(UploadImageFileFxController.class);
                }
                else if (Source.CAMERA.equals(imageSource)) {
                    setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);
                    renderUiAndWaitForUserInput(FaceCapturingFxController.class);
                }
                break;
            case 4:
                imageSource = (Source) this.getData(ImageSourceFxController.class, "imageSource");
                passData(PersonIdPaneFxController.class, ConfirmImageFxController.class, "personId");
                if (Source.UPLOAD.equals(imageSource)) {
                    passData(UploadImageFileFxController.class, "uploadedImage", ConfirmImageFxController.class,
                            "facePhoto");
                }
                else if (Source.CAMERA.equals(imageSource)) {
                    passData(FaceCapturingFxController.class, ConfirmImageFxController.class, "facePhoto");
                }

                renderUiAndWaitForUserInput(ConfirmImageFxController.class);
                break;
            case 5:
                verificationMethod = (VerificationMethod) this
                        .getData(VerificationMethodSelectionFxController.class, "verificationMethod");
                setData(VerificationProgressPaneFxController.class, "verificationMethod", verificationMethod);
                renderUiAndWaitForUserInput(VerificationProgressPaneFxController.class);
                if (VerificationMethod.FINGERPRINT.equals(verificationMethod)) {
                    incrementNSteps(-2);
                    FingerPosition selectedFingerprintPosition = (FingerPosition) this
                            .getData(SingleFingerprintCapturingFxController.class, "selectedFingerprintPosition");
                    passData(PersonIdPaneFxController.class, FingerprintVerificationWorkflowTask.class,
                            "personId");
                    passData(SingleFingerprintCapturingFxController.class, "capturedFingerprintForBackend",
                            FingerprintVerificationWorkflowTask.class, "fingerprint");
                    setData(FingerprintVerificationWorkflowTask.class, "fingerPosition",
                            selectedFingerprintPosition.getPosition());
                    executeWorkflowTask(FingerprintVerificationWorkflowTask.class);
                    MatchingResponse matchingResponse = (MatchingResponse) this
                            .getData(FingerprintVerificationWorkflowTask.class, "matchingResponse");
                    setData(VerificationProgressPaneFxController.class, "matched", matchingResponse.isMatched());
                }
                else if (VerificationMethod.FACE_PHOTO.equals(verificationMethod)) {
                    passData(PersonIdPaneFxController.class, FaceVerificationWorkflowTask.class, "personId");
                    passData(ConfirmImageFxController.class, FaceVerificationWorkflowTask.class,
                            "facePhotoBase64");
                    executeWorkflowTask(FaceVerificationWorkflowTask.class);
                    faceMatchingResponse = (FaceMatchingResponse) this
                            .getData(FaceVerificationWorkflowTask.class, "faceMatchingResponse");
                    setData(VerificationProgressPaneFxController.class, "matched",
                            faceMatchingResponse.isMatched());
                }
                break;
            case 6:
                verificationMethod = (VerificationMethod) this
                        .getData(VerificationMethodSelectionFxController.class, "verificationMethod");
                this.passData(PersonIdPaneFxController.class, ShowResultFxController.class, "personId");
                if (VerificationMethod.FINGERPRINT.equals(verificationMethod)) {
                    MatchingResponse matchingResponse = (MatchingResponse) this
                            .getData(FingerprintVerificationWorkflowTask.class, "matchingResponse");
                    setData(ShowResultFxController.class, "facePhoto",
                            AppUtils.imageFromBase64(matchingResponse.getPersonInfo().getFace()));
                    setData(ShowResultFxController.class, "personInfo", matchingResponse.getPersonInfo());
                }
                else if (VerificationMethod.FACE_PHOTO.equals(verificationMethod)) {
                    passData(ConfirmImageFxController.class, ShowResultFxController.class, "facePhoto");
                    faceMatchingResponse = (FaceMatchingResponse) this
                            .getData(FaceVerificationWorkflowTask.class, "faceMatchingResponse");
                    setData(ShowResultFxController.class, "personInfo", faceMatchingResponse.getPersonInfo());
                }

                renderUiAndWaitForUserInput(ShowResultFxController.class);
        }
    }
}

////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package sa.gov.nic.bio.bw.workflow.biometricsverification;
//
//        import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
//        import sa.gov.nic.bio.bw.core.utils.AppUtils;
//        import sa.gov.nic.bio.bw.core.utils.Device;
//        import sa.gov.nic.bio.bw.core.wizard.Step;
//        import sa.gov.nic.bio.bw.core.wizard.Wizard;
//        import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
//        import sa.gov.nic.bio.bw.core.workflow.Signal;
//        import sa.gov.nic.bio.bw.core.workflow.WithLookups;
//        import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
//        import sa.gov.nic.bio.bw.workflow.biometricsverification.beans.MatchingResponse;
//        import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.ShowResultFxController;
//        import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationMethodSelectionFxController;
//        import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationProgressPaneFxController;
//        import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationMethodSelectionFxController.VerificationMethod;
//        import sa.gov.nic.bio.bw.workflow.biometricsverification.tasks.FingerprintVerificationWorkflowTask;
//        import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
//        import sa.gov.nic.bio.bw.workflow.commons.controllers.SingleFingerprintCapturingFxController;
//        import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
//        import sa.gov.nic.bio.bw.workflow.faceverification.beans.FaceMatchingResponse;
//        import sa.gov.nic.bio.bw.workflow.faceverification.controllers.PersonIdPaneFxController;
//        import sa.gov.nic.bio.bw.workflow.faceverification.tasks.FaceVerificationWorkflowTask;
//        import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ConfirmImageFxController;
//        import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController;
//        import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.UploadImageFileFxController;
//        import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers.ImageSourceFxController.Source;
//
//@AssociatedMenu(
//        workflowId = 1028,
//        menuId = "menu.query.biometricsVerification",
//        menuTitle = "menu.title",
//        menuOrder = 11,
//        devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA, Device.IRIS_SCANNER}
//)
//
//@Wizard({@Step(
//        iconId = "\\uf2bb",
//        title = "wizard.personId"
//), @Step(
//        iconId = "question",
//        title = "wizard.selectVerificationMethod"
//), @Step(
//        iconId = "question",
//        title = "wizard.imageSource"
//), @Step(
//        iconId = "upload",
//        title = "wizard.uploadImage"
//), @Step(
//        iconId = "unlock",
//        title = "wizard.confirm"
//), @Step(
//        iconId = "\\uf248",
//        title = "wizard.matching"
//), @Step(
//        iconId = "file_text_alt",
//        title = "wizard.showResult"
//)})
//public class BiometricsVerificationWorkflow extends WizardWorkflowBase {
//    public BiometricsVerificationWorkflow() {
//    }
//
//    public void onStep(int step) throws InterruptedException, Signal {
//        VerificationMethod verificationMethod;
//        Source imageSource;
//        FaceMatchingResponse faceMatchingResponse;
//        switch(step) {
//            case 0:
//                this.renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
//                break;
//            case 1:
//                this.renderUiAndWaitForUserInput(VerificationMethodSelectionFxController.class);
//                break;
//            case 2:
//                verificationMethod = (VerificationMethod)this.getData(VerificationMethodSelectionFxController.class, "verificationMethod");
//                if (verificationMethod == VerificationMethod.FACE_PHOTO) {
//                    this.renderUiAndWaitForUserInput(ImageSourceFxController.class);
//                } else if (verificationMethod == VerificationMethod.FINGERPRINT) {
//                    this.incrementNSteps(2);
//                    this.setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprint", Boolean.TRUE);
//                    this.setData(SingleFingerprintCapturingFxController.class, "acceptBadQualityFingerprintMinRetires", 0);
//                    this.renderUiAndWaitForUserInput(SingleFingerprintCapturingFxController.class);
//                }
//                break;
//            case 3:
//                imageSource = (Source)this.getData(ImageSourceFxController.class, "imageSource");
//                if (Source.UPLOAD.equals(imageSource)) {
//                    this.renderUiAndWaitForUserInput(UploadImageFileFxController.class);
//                } else if (Source.CAMERA.equals(imageSource)) {
//                    this.setData(FaceCapturingFxController.class, "acceptAnyCapturedImage", true);
//                    this.renderUiAndWaitForUserInput(FaceCapturingFxController.class);
//                }
//                break;
//            case 4:
//                imageSource = (Source)this.getData(ImageSourceFxController.class, "imageSource");
//                this.passData(PersonIdPaneFxController.class, ConfirmImageFxController.class, "personId");
//                if (Source.UPLOAD.equals(imageSource)) {
//                    this.passData(UploadImageFileFxController.class, "uploadedImage", ConfirmImageFxController.class, "facePhoto");
//                } else if (Source.CAMERA.equals(imageSource)) {
//                    this.passData(FaceCapturingFxController.class, ConfirmImageFxController.class, "facePhoto");
//                }
//
//                this.renderUiAndWaitForUserInput(ConfirmImageFxController.class);
//                break;
//            case 5:
//                verificationMethod = (VerificationMethod)this.getData(VerificationMethodSelectionFxController.class, "verificationMethod");
//                this.setData(VerificationProgressPaneFxController.class, "verificationMethod", verificationMethod);
//                this.renderUiAndWaitForUserInput(VerificationProgressPaneFxController.class);
//                if (VerificationMethod.FINGERPRINT.equals(verificationMethod)) {
//                    this.incrementNSteps(-2);
//                    FingerPosition selectedFingerprintPosition = (FingerPosition)this.getData(SingleFingerprintCapturingFxController.class, "selectedFingerprintPosition");
//                    this.passData(PersonIdPaneFxController.class, FingerprintVerificationWorkflowTask.class, "personId");
//                    this.passData(SingleFingerprintCapturingFxController.class, "capturedFingerprintForBackend", FingerprintVerificationWorkflowTask.class, "fingerprint");
//                    this.setData(FingerprintVerificationWorkflowTask.class, "fingerPosition", selectedFingerprintPosition.getPosition());
//                    this.executeWorkflowTask(FingerprintVerificationWorkflowTask.class);
//                    MatchingResponse matchingResponse = (MatchingResponse)this.getData(FingerprintVerificationWorkflowTask.class, "matchingResponse");
//                    this.setData(VerificationProgressPaneFxController.class, "matched", matchingResponse.isMatched());
//                } else if (VerificationMethod.FACE_PHOTO.equals(verificationMethod)) {
//                    this.passData(PersonIdPaneFxController.class, FaceVerificationWorkflowTask.class, "personId");
//                    this.passData(ConfirmImageFxController.class, FaceVerificationWorkflowTask.class, "facePhotoBase64");
//                    this.executeWorkflowTask(FaceVerificationWorkflowTask.class);
//                    faceMatchingResponse = (FaceMatchingResponse)this.getData(FaceVerificationWorkflowTask.class, "faceMatchingResponse");
//                    this.setData(VerificationProgressPaneFxController.class, "matched", faceMatchingResponse.isMatched());
//                }
//                break;
//            case 6:
//                verificationMethod = (VerificationMethod)this.getData(VerificationMethodSelectionFxController.class, "verificationMethod");
//                this.passData(PersonIdPaneFxController.class, ShowResultFxController.class, "personId");
//                if (VerificationMethod.FINGERPRINT.equals(verificationMethod)) {
//                    MatchingResponse matchingResponse = (MatchingResponse)this.getData(FingerprintVerificationWorkflowTask.class, "matchingResponse");
//                    this.setData(ShowResultFxController.class, "facePhoto", AppUtils.imageFromBase64(matchingResponse.getPersonInfo().getFace()));
//                    this.setData(ShowResultFxController.class, "personInfo", matchingResponse.getPersonInfo());
//                } else if (VerificationMethod.FACE_PHOTO.equals(verificationMethod)) {
//                    this.passData(ConfirmImageFxController.class, ShowResultFxController.class, "facePhoto");
//                    faceMatchingResponse = (FaceMatchingResponse)this.getData(FaceVerificationWorkflowTask.class, "faceMatchingResponse");
//                    this.setData(ShowResultFxController.class, "personInfo", faceMatchingResponse.getPersonInfo());
//                }
//
//                this.renderUiAndWaitForUserInput(ShowResultFxController.class);
//        }
//
//    }
//}

