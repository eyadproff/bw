package sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers;

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
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.Map;

@FxmlFile("fingerprintsSource.fxml")
public class FingerprintsSourceFxController extends WizardStepFxControllerBase {
    public enum Source {
        SCANNING_FINGERPRINTS_CARD,
        CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER
    }

    @Input private Boolean showLiveScanOption;
    @Output private Source fingerprintsSource;

    @FXML private RadioButton rbByScanningFingerprintsCard;
    @FXML private RadioButton rbByCapturingFingerprintsViaScanner;
    @FXML private Button btnNext;
    @FXML private Button btnPrevious;

    private boolean incrementOneStep = false;

    @Override
    protected void onAttachedToScene() {
        if (showLiveScanOption != null && showLiveScanOption) {
            GuiUtils.showNode(rbByCapturingFingerprintsViaScanner, true);
            rbByCapturingFingerprintsViaScanner.setDisable(false);
        }


        if (fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD) {
            incrementOneStep = true;
            rbByScanningFingerprintsCard.setSelected(true);
            rbByScanningFingerprintsCard.requestFocus();
        }
        else {
            rbByCapturingFingerprintsViaScanner.setSelected(true);
            rbByCapturingFingerprintsViaScanner.requestFocus();
        }


        // go next on pressing ENTER on the radio buttons
        EventHandler<KeyEvent> eventHandler = event ->
        {
            if (event.getCode() == KeyCode.ENTER) {
                btnNext.fire();
                event.consume();
            }
        };

        rbByScanningFingerprintsCard.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
        rbByCapturingFingerprintsViaScanner.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);

        String fingerprintCapturingTitle = resources.getString("wizard.fingerprintCapturing");
        String scanFingerprintCardTitle = resources.getString("wizard.scanFingerprintCard");
        String specifyFingerprintCoordinatesTitle = resources.getString("wizard.specifyFingerprintCoordinates");

        int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex()).getStepIndexByTitle(fingerprintCapturingTitle);

        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(scanFingerprintCardTitle);
        }
        final int finalStepIndex = stepIndex;

        rbByCapturingFingerprintsViaScanner.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, fingerprintCapturingTitle,
                        "\\uf256");
                if (incrementOneStep) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex + 1);
                    incrementOneStep = false;
                }
            }
        });
        rbByScanningFingerprintsCard.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue) {
                Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, scanFingerprintCardTitle,
                        "file");

                if (!incrementOneStep) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex + 1,
                            specifyFingerprintCoordinatesTitle,
                            "\\uf247");
                    incrementOneStep = true;
                }
                else {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex + 1,
                            specifyFingerprintCoordinatesTitle,
                            "\\uf247");
                }
            }
        });

        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
                Context.getCoreFxController().getDeviceManagerGadgetPaneController();

        if (!deviceManagerGadgetPaneController.isDevicesRunnerRunning()) {
            deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
        }
    }

    @Override
    public void onGoingNext(Map<String, Object> uiDataMap) {

        if (rbByScanningFingerprintsCard.isSelected()) { fingerprintsSource = Source.SCANNING_FINGERPRINTS_CARD; }
        else if (rbByCapturingFingerprintsViaScanner.isSelected()) {
            fingerprintsSource =
                    Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER;
        }
    }
}