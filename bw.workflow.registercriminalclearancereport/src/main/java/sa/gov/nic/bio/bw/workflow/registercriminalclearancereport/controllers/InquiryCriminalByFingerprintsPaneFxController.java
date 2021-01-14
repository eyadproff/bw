package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.beans.WatchListRecord;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.tasks.FingerprintInquiryCriminalStatusCheckerWorkflowTask.Status;

import java.util.List;

@FxmlFile("inquiryCriminalByFingerprints.fxml")
public class InquiryCriminalByFingerprintsPaneFxController extends WizardStepFxControllerBase {
    @Input(requiredOnReturn = true) private Status status;
    @Input private Long criminalBiometricsId;
    @Input private List<WatchListRecord> watchListRecordList;

    @FXML private VBox paneError;
    @FXML private VBox paneHitCriminal;
    @FXML private VBox paneNotHitCriminal;
    @FXML private VBox paneHitCWL;
    @FXML private VBox paneNotHitCWL;
    @FXML private TableView<WatchListRecord> tvCWLActions;
    @FXML private TableColumn<WatchListRecord, WatchListRecord> tcSequence;
    @FXML private TableColumn<WatchListRecord, String> tcSamisId;
//    @FXML private TableColumn<WatchListRecord, String> tcIssuer;
    @FXML private TableColumn<WatchListRecord, String> tcAction;
    @FXML private VBox paneDevicesRunnerNotRunning;
    @FXML private ProgressIndicator piProgress;
    @FXML private Label lblProgress;
    @FXML private Label lblCanceling;
    @FXML private Label lblCancelled;
    @FXML private Label txtCriminalBiometricsId;
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

        if (!btnRegister.isDisable()) { btnRegister.requestFocus(); }
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
            else if (status == Status.HIT) {
                showHitCriminalProgress(criminalBiometricsId != null);
                showHitCWLProgress(watchListRecordList != null && !watchListRecordList.isEmpty());
            }
            else {
                showHitCriminalProgress(false);
                showHitCWLProgress(false);
            }
        }
        else { showProgress(false); }

        if (!btnRegister.isDisable()) { btnRegister.requestFocus(); }
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
        GuiUtils.showNode(paneHitCriminal, false);
        GuiUtils.showNode(lblCanceling, false);
        GuiUtils.showNode(lblCancelled, false);
    }

    private void showHitCriminalProgress(boolean bShow) {

        GuiUtils.showNode(piProgress, false);
        GuiUtils.showNode(lblProgress, false);
        GuiUtils.showNode(btnCancel, false);
        GuiUtils.showNode(btnRetry, false);
        GuiUtils.showNode(btnRegister, true);
        GuiUtils.showNode(btnStartOver, true);
        GuiUtils.showNode(paneError, false);
        GuiUtils.showNode(paneNotHitCriminal, !bShow);
        GuiUtils.showNode(paneHitCriminal, bShow);
        GuiUtils.showNode(lblCanceling, false);
        GuiUtils.showNode(lblCancelled, false);

        if (bShow && criminalBiometricsId != null) { txtCriminalBiometricsId.setText(AppUtils.localizeNumbers(criminalBiometricsId.toString())); }

    }

    private void showHitCWLProgress(boolean bShow) {

        GuiUtils.showNode(piProgress, false);
        GuiUtils.showNode(lblProgress, false);
        GuiUtils.showNode(btnCancel, false);
        GuiUtils.showNode(btnRetry, false);
        GuiUtils.showNode(btnRegister, true);
        GuiUtils.showNode(btnStartOver, true);
        GuiUtils.showNode(paneError, false);
        GuiUtils.showNode(paneNotHitCWL, !bShow);
        GuiUtils.showNode(paneHitCWL, bShow);
        GuiUtils.showNode(lblCanceling, false);
        GuiUtils.showNode(lblCancelled, false);

        if (bShow && watchListRecordList != null && !watchListRecordList.isEmpty()) {
            GuiUtils.initSequenceTableColumn(tcSequence);
            tcSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));

            tcSamisId.setCellValueFactory(param ->
            {
                Long samisId = param.getValue().getSamisId();
                return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(samisId)));
            });

            //            tcIssuer.setCellValueFactory(param ->
            //            {
            //                String issuer = param.getValue().();
            //                return new SimpleStringProperty(issuer);
            //            });


            tcAction.setCellValueFactory(param ->
            {
                String action = param.getValue().getActionMessage();
                return new SimpleStringProperty(action);
            });

            tvCWLActions.getItems().setAll(watchListRecordList);
        }

    }
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