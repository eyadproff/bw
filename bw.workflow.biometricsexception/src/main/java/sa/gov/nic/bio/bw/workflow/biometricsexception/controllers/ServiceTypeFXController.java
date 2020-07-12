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

@FxmlFile("serviceType.fxml")
public class ServiceTypeFXController extends WizardStepFxControllerBase {

    @Output
    private ServiceTypeFXController.ServiceType serviceType;
    @FXML
    private RadioButton rbAddOrEdit;
    @FXML
    private RadioButton rbDelete;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;

    @Override
    protected void onAttachedToScene() {


        EventHandler<KeyEvent> eventHandler = event ->
        {
            if (event.getCode() == KeyCode.ENTER) {
                btnNext.fire();
                event.consume();
            }
        };
        rbAddOrEdit.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
        rbDelete.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);

        String AddOrEditFingerprintExceptionTitle = resources.getString("wizard.addOrEditMissingFingerPrint");
        String DeleteFingerprintExceptionTitle = resources.getString("wizard.deleteMissingFingerPrint");

        // change the wizard-step-indicator upon changing the image source
        int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                .getStepIndexByTitle(AddOrEditFingerprintExceptionTitle);
        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(DeleteFingerprintExceptionTitle);
        }

        final int finalStepIndex = stepIndex;

        rbAddOrEdit.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                Context.getCoreFxController().getWizardPane(getTabIndex())
                        .updateStep(finalStepIndex, AddOrEditFingerprintExceptionTitle,
                                "\\uf256");
            }
            else {
                Context.getCoreFxController().getWizardPane(getTabIndex())
                        .updateStep(finalStepIndex, DeleteFingerprintExceptionTitle,
                                "\\uf256");
            }
        });

        // if (hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);

        // load the old state, if exists
        if (ServiceTypeFXController.ServiceType.DELETE_FINGERPRINTS.equals(serviceType)) {
            rbDelete.setSelected(true);
            rbDelete.requestFocus();
        }
        else {
            rbAddOrEdit.setSelected(true);
            rbAddOrEdit.requestFocus();
        }

    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {
        onGoingNext(uiDataMap);

    }

    @Override
    public void onGoingNext(Map<String, Object> uiDataMap) {
        if (rbDelete.isSelected()) { serviceType = ServiceTypeFXController.ServiceType.DELETE_FINGERPRINTS; }
        else { serviceType = ServiceTypeFXController.ServiceType.ADD_OR_EDIT_FINGERPRINTS; }
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) { goNext(); }
    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        if (rbDelete.isSelected()) { serviceType = ServiceTypeFXController.ServiceType.DELETE_FINGERPRINTS; }
        else { serviceType = ServiceTypeFXController.ServiceType.ADD_OR_EDIT_FINGERPRINTS; }
        continueWorkflow();
    }

    public enum ServiceType {
        ADD_OR_EDIT_FINGERPRINTS,
        DELETE_FINGERPRINTS

    }

}
