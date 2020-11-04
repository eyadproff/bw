package sa.gov.nic.bio.bw.workflow.biometricsinquiry.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.Map;

@FxmlFile("inquiryMethod.fxml")
public class inquiryMethodSelectionFxController extends WizardStepFxControllerBase {
    public enum InquiryMethod {
        FINGERPRINT,
        FACE_PHOTO,
        IRIS
    }

    @Output private InquiryMethod inquiryMethod;

    @FXML private RadioButton rbByFingerprint;
    @FXML private RadioButton rbByFacePhoto;
    @FXML private RadioButton rbByIris;
    @FXML private Button btnNext;


    private boolean minusStep = false;
    private boolean minus2Steps = false;


    @Override
    protected void onAttachedToScene() {

        // load the old state, if exists
        if (InquiryMethod.FINGERPRINT.equals(inquiryMethod)) {
//            rbByFingerprint.setSelected(true);
//            rbByFingerprint.requestFocus();
            minusStep = true;
        }
        else if (InquiryMethod.IRIS.equals(inquiryMethod)) {
//            rbByIris.setSelected(true);
//            rbByIris.requestFocus();
            minus2Steps = true;
        }
        else {
//            rbByFacePhoto.setSelected(true);
//            rbByFacePhoto.requestFocus();
        }


        // go next on pressing ENTER on the radio buttons
        EventHandler<KeyEvent> eventHandler = event ->
        {
            if (event.getCode() == KeyCode.ENTER) {
                btnNext.fire();
                event.consume();
            }
        };
        rbByFingerprint.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
        rbByFacePhoto.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
        rbByIris.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);


        String fingerprintCapturingTitle = resources.getString("wizard.singleFingerprintCapturing");
        String showFingerprintsViewTitle = resources.getString("wizard.showFingerprintsView");
        String facePhotoSourceTitle = resources.getString("wizard.imageSource");
        String irisCaptureTitle = resources.getString("wizard.irisCapturing");


        // change the wizard-step-indicator upon changing the Inquiry method
        int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                .getStepIndexByTitle(fingerprintCapturingTitle);
        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(facePhotoSourceTitle);
        }
        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(irisCaptureTitle);
        }


        final int finalStepIndex = stepIndex;

        rbByFingerprint.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {

                Context.getCoreFxController()
                        .getWizardPane(getTabIndex()).updateStep(finalStepIndex,
                        fingerprintCapturingTitle,
                        "\\uf256");


                if (minus2Steps) {
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex()).addStep(finalStepIndex + 1,
                            showFingerprintsViewTitle,
                            "\\uf256");
                    minusStep = true;
                    minus2Steps = false;
                }
                else if (!minusStep) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);
                    // Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex()).updateStep(finalStepIndex + 1,
                            showFingerprintsViewTitle,
                            "\\uf256");
                    minusStep = true;
                }
                else {
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex()).updateStep(finalStepIndex + 1,
                            showFingerprintsViewTitle,
                            "\\uf256");
                }

            }

        });

        rbByFacePhoto.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                Context.getCoreFxController()
                        .getWizardPane(getTabIndex()).updateStep(finalStepIndex, facePhotoSourceTitle,
                        "question");


                if (minus2Steps) {
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex())
                            .addStep(finalStepIndex + 1, resources.getString("wizard.uploadImage"),
                                    "upload");
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex()).addStep(finalStepIndex + 2, resources.getString(
                            "wizard.confirm"),
                            "unlock");

                    minus2Steps = false;
                }

                else if (minusStep) {
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex())
                            .addStep(finalStepIndex + 1, resources.getString("wizard.uploadImage"),
                                    "upload");

                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex()).updateStep(finalStepIndex + 2, resources.getString(
                            "wizard.confirm"),
                            "unlock");
                    minusStep = false;
                }
                else {
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex())
                            .updateStep(finalStepIndex + 1, resources.getString("wizard.uploadImage"),
                                    "upload");
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex()).updateStep(finalStepIndex + 2, resources.getString(
                            "wizard.confirm"),
                            "unlock");


                }
            }
        });

        rbByIris.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {

                Context.getCoreFxController()
                        .getWizardPane(getTabIndex()).updateStep(finalStepIndex,
                        irisCaptureTitle,
                        "eye");

                if (minusStep) {
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);

                    minus2Steps = true;
                    minusStep = false;
                }
                else if (!minus2Steps) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);

                    minus2Steps = true;
                }

            }

        });


        // load the old state, if exists
        if (InquiryMethod.FINGERPRINT.equals(inquiryMethod)) {
            rbByFingerprint.setSelected(true);
            rbByFingerprint.requestFocus();
        }
        else if (InquiryMethod.IRIS.equals(inquiryMethod)) {
            rbByIris.setSelected(true);
            rbByIris.requestFocus();
        }
        else {
            rbByFacePhoto.setSelected(true);
            rbByFacePhoto.requestFocus();
        }


        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
                Context.getCoreFxController().getDeviceManagerGadgetPaneController();

        if (!deviceManagerGadgetPaneController.isDevicesRunnerRunning()) {
            deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
        }

    }


    @Override
    public void onGoingNext(Map<String, Object> uiDataMap) {
        if (rbByFacePhoto.isSelected()) { inquiryMethod = InquiryMethod.FACE_PHOTO; }
        else if (rbByFingerprint.isSelected()) { inquiryMethod = InquiryMethod.FINGERPRINT; }
        else { inquiryMethod = InquiryMethod.IRIS; }
    }
}