package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

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

@FxmlFile("biometricsExceptionType.fxml")
public class BiometricsExceptionTypeFXController extends WizardStepFxControllerBase {

    @Output
    private Type exceptionType;
    @FXML
    private RadioButton rbFingerPrints;
    @FXML
    private RadioButton rbFaceImage;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;

    private boolean minusOneStep = false;

    @Override
    protected void onAttachedToScene() {
        if (Type.FACE.equals(exceptionType)) {
            rbFaceImage.setSelected(true);
            rbFaceImage.requestFocus();
            minusOneStep = true;
        } else {
            rbFingerPrints.setSelected(true);
            rbFingerPrints.requestFocus();
        }
        // go next on pressing ENTER on the radio buttons
        EventHandler<KeyEvent> eventHandler = event ->
        {
            if (event.getCode() == KeyCode.ENTER) {
                btnNext.fire();
                event.consume();
            }
        };
        rbFingerPrints.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
        rbFaceImage.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);

        String ServiceTypeTitle = resources.getString("wizard.serviceType");
        String FaceImageExceptionTitle = resources.getString("wizard.FaceImage");
        String AddOrEditFingerprintExceptionTitle = resources.getString("wizard.addOrEditMissingFingerPrint");

        // change the wizard-step-indicator upon changing the image source
        int stepIndex = Context.getCoreFxController().getWizardPane().getStepIndexByTitle(ServiceTypeTitle);
        if (stepIndex < 0) stepIndex = Context.getCoreFxController().getWizardPane()
                .getStepIndexByTitle(FaceImageExceptionTitle);

        final int finalStepIndex = stepIndex;

        rbFingerPrints.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                Context.getCoreFxController().getWizardPane().updateStep(finalStepIndex, ServiceTypeTitle,
                        "question");
                if (minusOneStep) {
                    Context.getCoreFxController().getWizardPane().addStep(finalStepIndex + 1,
                            AddOrEditFingerprintExceptionTitle,
                            "\\uf256");
                    minusOneStep = false;
                } else {
                    Context.getCoreFxController().getWizardPane().updateStep(finalStepIndex + 1,
                            AddOrEditFingerprintExceptionTitle,
                            "\\uf256");
                }
            } else {
                Context.getCoreFxController().getWizardPane().updateStep(finalStepIndex, FaceImageExceptionTitle,
                        "user");
                if (!minusOneStep) {
                    Context.getCoreFxController().getWizardPane().removeStep(finalStepIndex + 1);
                    minusOneStep = true;
                }

            }
        });

        // if (hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);

        // load the old state, if exists

    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {
        onGoingNext(uiDataMap);
    }

    @Override
    public void onGoingNext(Map<String, Object> uiDataMap) {
        if (rbFaceImage.isSelected()) exceptionType = Type.FACE;
        else exceptionType = Type.FINGERPRINTS;
    }

    public enum Type {
        FINGERPRINTS,
        FACE

    }
}
