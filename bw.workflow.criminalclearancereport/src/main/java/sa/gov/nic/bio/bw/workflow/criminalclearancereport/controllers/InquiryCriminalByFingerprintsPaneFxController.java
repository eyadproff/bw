package sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.FingerprintInquiryCriminalStatusCheckerWorkflowTask.Status;

@FxmlFile("inquiryCriminalByFingerprints.fxml")
public class InquiryCriminalByFingerprintsPaneFxController extends WizardStepFxControllerBase {
    @Input(requiredOnReturn = true) private Status status;
    @Input private Long criminalBiometricsId;

    @FXML private VBox paneError;
    @FXML private VBox paneNotHit;
    @FXML private VBox paneHitCriminal;
    @FXML private VBox paneNotHitCriminal;
    @FXML private VBox paneDevicesRunnerNotRunning;
    @FXML private ProgressIndicator piProgress;
    @FXML private Label lblProgress;
    @FXML private Label lblCanceling;
    @FXML private Label lblCancelled;
    @FXML private TextField txtCriminalBiometricsId;
    @FXML private Button btnCancel;
    @FXML private Button btnRetry;
    @FXML private Button btnStartOver;
    @FXML private Button btnRegister;

    private boolean fingerprintInquiryCancelled = false;

    @Override
    protected void onAttachedToScene() {
        btnCancel.setOnAction(actionEvent ->
        {
            GuiUtils.showNode(lblProgress, false);
            GuiUtils.showNode(btnCancel, false);
            GuiUtils.showNode(lblCanceling, true);
            fingerprintInquiryCancelled = true;
        });

        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
                Context.getCoreFxController().getDeviceManagerGadgetPaneController();
        deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
        {
            GuiUtils.showNode(piProgress, running);
            GuiUtils.showNode(lblProgress, running);
            GuiUtils.showNode(btnCancel, running);
            GuiUtils.showNode(paneDevicesRunnerNotRunning, !running);
            GuiUtils.showNode(btnStartOver, !running);

            if (running) {
                showProgress(true);
                continueWorkflow();
            }
        });

        if (!deviceManagerGadgetPaneController.isDevicesRunnerRunning()) {
            showProgress(false);
            GuiUtils.showNode(paneDevicesRunnerNotRunning, true);
            deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
        }
        else {
            showProgress(true);
            continueWorkflow();
        }
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {

        if (successfulResponse) {
            if (status == Status.PENDING) {
                btnCancel.setDisable(false);
                Context.getExecutorService().submit(() ->
                {
                    int seconds = Integer.parseInt(
                            Context.getConfigManager().getProperty("fingerprint.inquiry.checkEverySeconds"));

                    try {
                        Thread.sleep(seconds * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(() ->
                    {
                        if (fingerprintInquiryCancelled) {
                            fingerprintInquiryCancelled = false;
                            GuiUtils.showNode(piProgress, false);
                            GuiUtils.showNode(lblCanceling, false);
                            GuiUtils.showNode(lblCancelled, true);
                            GuiUtils.showNode(btnStartOver, true);
                            GuiUtils.showNode(btnRetry, true);
                        }
                        else { continueWorkflow(); }
                    });
                });
            }
            else if (status == Status.HIT && criminalBiometricsId !=null) {
                showHitCriminalProgress(true);
            }
            else {
                showHitCriminalProgress(false);
            }
//            }else {
//                showHitProgress(false);
//            }
        }
        else { showProgress(false); }
    }

    private void showProgress(boolean bShow) {
        GuiUtils.showNode(piProgress, bShow);
        GuiUtils.showNode(lblProgress, bShow);
        GuiUtils.showNode(btnCancel, bShow);
        GuiUtils.showNode(btnRetry, !bShow);
        GuiUtils.showNode(btnRegister, false);
        GuiUtils.showNode(btnStartOver, !bShow);
        GuiUtils.showNode(paneError, !bShow);
        GuiUtils.showNode(paneNotHitCriminal, false);
        GuiUtils.showNode(paneNotHit,false);
        GuiUtils.showNode(paneHitCriminal, false);
        GuiUtils.showNode(lblCanceling, false);
        GuiUtils.showNode(lblCancelled, false);
    }

    private void showHitCriminalProgress(boolean bShow) {

        GuiUtils.showNode(piProgress, false);
        GuiUtils.showNode(lblProgress, false);
        GuiUtils.showNode(btnCancel, false);
        GuiUtils.showNode(btnRetry, bShow);
        GuiUtils.showNode(btnRegister, true);
        GuiUtils.showNode(btnStartOver, true);
        GuiUtils.showNode(paneError, false);
        GuiUtils.showNode(paneNotHitCriminal, !bShow);
        GuiUtils.showNode(paneNotHit,false);
        GuiUtils.showNode(paneHitCriminal, bShow);
        GuiUtils.showNode(lblCanceling, false);
        GuiUtils.showNode(lblCancelled, false);

        if (bShow && criminalBiometricsId != null) { txtCriminalBiometricsId.setText(criminalBiometricsId.toString()); }

    }

//    private void showHitProgress(boolean bShow) {
//
//        GuiUtils.showNode(piProgress, false);
//        GuiUtils.showNode(lblProgress, false);
//        GuiUtils.showNode(btnCancel, false);
//        GuiUtils.showNode(btnRetry, !bShow);
//        GuiUtils.showNode(btnRegister, false);
//        GuiUtils.showNode(btnStartOver, !bShow);
//        GuiUtils.showNode(paneError, false);
//        GuiUtils.showNode(paneNotHitCriminal, false);
//        GuiUtils.showNode(paneNotHit,!bShow);
//        GuiUtils.showNode(paneHitCriminal, false);
//        GuiUtils.showNode(lblCanceling, false);
//        GuiUtils.showNode(lblCancelled, false);
//
//        if (bShow && criminalBiometricsId != null) { txtCriminalBiometricsId.setText(criminalBiometricsId.toString()); }
//
//    }

    @Override
    protected void onDetachingFromScene() {
        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
                Context.getCoreFxController().getDeviceManagerGadgetPaneController();
        deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(null);
    }

    @FXML
    private void onRetryButtonClicked(ActionEvent actionEvent) {
        showProgress(true);
        continueWorkflow();
    }

    @FXML
    private void onRegisterClicked(ActionEvent actionEvent) {
        goNext();
    }
}