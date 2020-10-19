package sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;

@FxmlFile("inquiryByFingerprints.fxml")
public class InquiryByFingerprintsPaneFxController extends WizardStepFxControllerBase {
    @Input(requiredOnReturn = true) private Status status;

    @FXML private VBox paneError;
    @FXML private VBox paneNotHit;
    @FXML private VBox paneDevicesRunnerNotRunning;
    @FXML private ProgressIndicator piProgress;
    @FXML private Label lblProgress;
    @FXML private Label lblCanceling;
    @FXML private Label lblCancelled;
    @FXML private Button btnCancel;
    @FXML private Button btnRetry;
    @FXML private Button btnStartOver;
    @FXML private Button btnTakeFingerprints;

    private boolean fingerprintInquiryCancelled = false;
    private boolean minusOneStepAndAddThree = false;

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
        String showingQualityFingerprintsView = resources.getString("wizard.showQualityFingerprintsView");
        String showingFingerprintsView = resources.getString("wizard.showFingerprintsView");
        String selectFingerprintsSource = resources.getString("wizard.selectFingerprintsSource");
        String fingerprintCapturing = resources.getString("wizard.fingerprintCapturing");

        int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex()).getStepIndexByTitle(showingQualityFingerprintsView);

        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(selectFingerprintsSource);
        }
        final int finalStepIndex = stepIndex;

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
                if (minusOneStepAndAddThree) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex);
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex);
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex);
                    Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex,
                            showingQualityFingerprintsView,
                            "\\uf256");
                    minusOneStepAndAddThree = false;
                }
                goNext();
            }
            else {

               if(!minusOneStepAndAddThree) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex);
                   Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex,
                           selectFingerprintsSource,
                           "question");
                   Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex+1,
                           fingerprintCapturing,
                           "\\uf256");
                    Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex+2,
                            showingFingerprintsView,
                            "\\uf256");

                    minusOneStepAndAddThree = true;
                }
                showNotHitProgress(true);
            }
        }
        else { showProgress(false); }
    }

    private void showProgress(boolean bShow) {
        GuiUtils.showNode(piProgress, bShow);
        GuiUtils.showNode(lblProgress, bShow);
        GuiUtils.showNode(btnCancel, bShow);
        GuiUtils.showNode(btnRetry, !bShow);
        GuiUtils.showNode(btnTakeFingerprints, false);
        GuiUtils.showNode(btnStartOver, !bShow);
        GuiUtils.showNode(paneError, !bShow);
        GuiUtils.showNode(paneNotHit, false);
        GuiUtils.showNode(lblCanceling, false);
        GuiUtils.showNode(lblCancelled, false);
    }

    private void showNotHitProgress(boolean bShow) {

        GuiUtils.showNode(piProgress, !bShow);
        GuiUtils.showNode(lblProgress, !bShow);
        GuiUtils.showNode(btnCancel, !bShow);
        GuiUtils.showNode(btnRetry, bShow);
        GuiUtils.showNode(btnTakeFingerprints, bShow);
        GuiUtils.showNode(btnStartOver, bShow);
        GuiUtils.showNode(paneError, !bShow);
        GuiUtils.showNode(paneNotHit, bShow);
        GuiUtils.showNode(lblCanceling, false);
        GuiUtils.showNode(lblCancelled, false);


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
    private void onTakeFingerprintsClicked(ActionEvent actionEvent) {
        goNext();
    }
}