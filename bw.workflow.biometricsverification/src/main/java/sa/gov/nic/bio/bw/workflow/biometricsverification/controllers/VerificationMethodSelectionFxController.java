package sa.gov.nic.bio.bw.workflow.biometricsverification.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.Map;

@FxmlFile("verificationMethod.fxml")
public class VerificationMethodSelectionFxController extends WizardStepFxControllerBase {
    public enum VerificationMethod {
        FINGERPRINT,
        FACE_PHOTO
    }

    @Output private VerificationMethod verificationMethod;

    @FXML private RadioButton rbByFingerprint;
    @FXML private RadioButton rbByFacePhoto;
    @FXML private Button btnStartOver;
    @FXML private Button btnNext;

    private boolean minusSteps = false;

    @Override
    protected void onAttachedToScene() {

        // load the old state, if exists
        if (VerificationMethod.FINGERPRINT.equals(verificationMethod)) {
            rbByFingerprint.setSelected(true);
            rbByFingerprint.requestFocus();
            minusSteps = true;
        }
        else {
            rbByFacePhoto.setSelected(true);
            rbByFacePhoto.requestFocus();
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


        String fingerprintCapturingTitle = resources.getString("wizard.singleFingerprintCapturing");
        String facePhotoSourceTitle = resources.getString("wizard.imageSource");

        // change the wizard-step-indicator upon changing the verification method
        int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                .getStepIndexByTitle(fingerprintCapturingTitle);
        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(facePhotoSourceTitle);
        }

        final int finalStepIndex = stepIndex;

        rbByFingerprint.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {

                Context.getCoreFxController()
                        .getWizardPane(getTabIndex()).updateStep(finalStepIndex,
                        fingerprintCapturingTitle,
                        "\\uf25a");

                if (!minusSteps) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);
                    minusSteps = true;
                }
            }

        });

        rbByFacePhoto.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                Context.getCoreFxController()
                        .getWizardPane(getTabIndex()).updateStep(finalStepIndex, facePhotoSourceTitle,
                        "question");

                if (minusSteps) {
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex())
                            .addStep(finalStepIndex + 1, resources.getString("wizard.uploadImage"),
                                    "upload");
                    Context.getCoreFxController()
                            .getWizardPane(getTabIndex()).addStep(finalStepIndex + 2, resources.getString("wizard" +
                                                                                                          ".confirm"),
                            "unlock");
                    minusSteps = false;
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


    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {
        onGoingNext(uiDataMap);
    }

    @Override
    public void onGoingNext(Map<String, Object> uiDataMap) {
        if (rbByFacePhoto.isSelected()) { verificationMethod = VerificationMethod.FACE_PHOTO; }
        else { verificationMethod = VerificationMethod.FINGERPRINT; }
    }

    @FXML
    protected void onNextButtonClicked(ActionEvent actionEvent)
    {
        continueWorkflow();
    }
    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {

        if (successfulResponse) {
            goNext();
        }
    }
}