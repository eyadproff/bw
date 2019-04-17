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
    private Type imageSource;
    @FXML
    private RadioButton rbFingerPrints;
    @FXML
    private RadioButton rbFaceImage;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;

    @Override
    protected void onAttachedToScene() {
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

        String FingerprintExceptionTitle = resources.getString("wizard.editMissingFingerPrint");
        String FaceImageExceptionTitle = resources.getString("wizard.FaceImage");

        // change the wizard-step-indicator upon changing the image source
        int stepIndex = Context.getCoreFxController().getWizardPane().getStepIndexByTitle(FingerprintExceptionTitle);
        if (stepIndex < 0) stepIndex = Context.getCoreFxController().getWizardPane()
                .getStepIndexByTitle(FaceImageExceptionTitle);

        final int finalStepIndex = stepIndex;

        rbFingerPrints.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue)
                Context.getCoreFxController().getWizardPane().updateStep(finalStepIndex, FingerprintExceptionTitle,
                        "\\uf256");
            else Context.getCoreFxController().getWizardPane().updateStep(finalStepIndex, FaceImageExceptionTitle,
                    "user");
        });

        // if (hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);

        // load the old state, if exists
        if (Type.FACEIMAGE.equals(imageSource)) {
            rbFaceImage.setSelected(true);
            rbFaceImage.requestFocus();
        } else {
            rbFingerPrints.setSelected(true);
            rbFingerPrints.requestFocus();
        }
    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {
        onGoingNext(uiDataMap);
    }

    @Override
    public void onGoingNext(Map<String, Object> uiDataMap) {
        if (rbFaceImage.isSelected()) imageSource = Type.FACEIMAGE;
        else imageSource = Type.FINGERPRINTS;
    }

    public enum Type {
        FINGERPRINTS,
        FACEIMAGE

    }
}
