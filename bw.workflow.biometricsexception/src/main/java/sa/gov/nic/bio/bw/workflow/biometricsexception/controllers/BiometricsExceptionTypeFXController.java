package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

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

        // change the wizard-step-indicator upon changing the ExceptionType
        int stepIndex =
                Context.getCoreFxController().getWizardPane(getTabIndex()).getStepIndexByTitle(ServiceTypeTitle);
        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(FaceImageExceptionTitle);
        }

        final int finalStepIndex = stepIndex;

        rbFingerPrints.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, ServiceTypeTitle,
                        "question");
                if (minusOneStep) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex + 1,
                            AddOrEditFingerprintExceptionTitle,
                            "\\uf256");
                    minusOneStep = false;
                }
                else {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex + 1,
                            AddOrEditFingerprintExceptionTitle,
                            "\\uf256");
                }
            }
            else {
                Context.getCoreFxController().getWizardPane(getTabIndex())
                        .updateStep(finalStepIndex, FaceImageExceptionTitle,
                                "user");
                if (!minusOneStep) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);
                    minusOneStep = true;
                }

            }
        });

        // if (hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);

        // load the old state, if exists
        if (Type.FACE.equals(exceptionType)) {
            rbFaceImage.setSelected(true);
            rbFaceImage.requestFocus();
            minusOneStep = true;
        }
        else {
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
        if (rbFaceImage.isSelected()) { exceptionType = Type.FACE; }
        else { exceptionType = Type.FINGERPRINTS; }
    }


    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) { goNext(); }
    }

    @FXML
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        if (rbFaceImage.isSelected()) { exceptionType = Type.FACE; }
        else { exceptionType = Type.FINGERPRINTS; }

        continueWorkflow();
    }

    public enum Type {
        FINGERPRINTS,
        FACE

    }
}
