package sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.CheckCitizenRegistrationWorkflowTask.Status;

@FxmlFile("registeringCitizen.fxml")
public class RegisteringCitizenPaneFxController extends WizardStepFxControllerBase {
    public enum Request {
        SUBMIT_CITIZEN_REGISTRATION,
        CHECK_CITIZEN_REGISTRATION
    }

    @Input private Status citizenRegistrationStatus;
    @Input private Boolean isEnrollmentProcessStart;
    @Output private Request request;

    @FXML private ProgressIndicator CPiProgress;
    @FXML private ImageView CitizenIvSuccess;
    @FXML private ImageView CitizenIvFailure;
    @FXML private ImageView CitizenIvSuccessWithHit;

    @FXML private Label CitizenLblStatus;

    @FXML private Button btnRetry;
    @FXML private Button btnStartOver;


    private boolean disableRetryButtonForever = false;


    @Override
    protected void onAttachedToScene() {
        request = Request.SUBMIT_CITIZEN_REGISTRATION;
        continueWorkflow();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (request == Request.SUBMIT_CITIZEN_REGISTRATION) {
            if (successfulResponse && isEnrollmentProcessStart) {
                CitizenLblStatus.setText(resources.getString("label.waitingCitizenRegistration"));
                request = Request.CHECK_CITIZEN_REGISTRATION;
                continueWorkflow();
            }
            else {
                CitizenLblStatus.setText(resources.getString("label.failedToSendCitizenInfo"));
                GuiUtils.showNode(CPiProgress, false);
                GuiUtils.showNode(CitizenIvFailure, true);
                GuiUtils.showNode(btnStartOver, true);
                GuiUtils.showNode(btnRetry, !disableRetryButtonForever);

            }
        }
        else if (request == Request.CHECK_CITIZEN_REGISTRATION) {
            if (successfulResponse) {
                if (citizenRegistrationStatus == Status.PENDING) {
                    Context.getExecutorService().submit(() -> {
                        try {
                            int seconds = Integer.parseInt(Context.getConfigManager().getProperty(
                                    "citizenEnrollment.inquiry.checkEverySeconds"));
                            Thread.sleep(seconds * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Platform.runLater(this::continueWorkflow);
                    });

                }
                else if (citizenRegistrationStatus == Status.SUCCESS) {
                    CitizenLblStatus.setText(resources.getString("label.successCitizenRegistration"));
                    GuiUtils.showNode(CPiProgress, false);
                    GuiUtils.showNode(CitizenIvSuccess, true);
                    GuiUtils.showNode(btnStartOver, true);
                }

                else if (citizenRegistrationStatus == Status.HIT) {
                    CitizenLblStatus.setText(resources.getString("label.successCitizenRegistrationWithHitResponse"));
                    GuiUtils.showNode(CPiProgress, false);
                    GuiUtils.showNode(CitizenIvSuccessWithHit, true);
                    GuiUtils.showNode(btnStartOver, true);
                }
                else if (citizenRegistrationStatus == Status.ERROR) {
                    CitizenLblStatus.setText(resources.getString("label.failedToRegisterCitizen"));
                    GuiUtils.showNode(CPiProgress, false);
                    GuiUtils.showNode(CitizenIvFailure, true);
                    GuiUtils.showNode(btnStartOver, true);
                    GuiUtils.showNode(btnRetry, !disableRetryButtonForever);
                }


            }
            else {
                CitizenLblStatus.setText(resources.getString("label.failedToRegisterCitizen"));
                GuiUtils.showNode(CPiProgress, false);
                GuiUtils.showNode(CitizenIvFailure, true);
                GuiUtils.showNode(btnStartOver, true);
                GuiUtils.showNode(btnRetry, !disableRetryButtonForever);
            }
        }
    }

    @Override
    public void reportNegativeTaskResponse(String errorCode, Throwable exception, String[] errorDetails) {
        //Failed to register the iris! The request cannot be resent.
        if ("B003-0066".equals(errorCode)) {
            disableRetryButtonForever = true;
        }

        super.reportNegativeTaskResponse(errorCode, exception, errorDetails);
    }

    @FXML
    private void onRetryButtonClicked(ActionEvent actionEvent) {
        GuiUtils.showNode(btnRetry, false);
        GuiUtils.showNode(btnStartOver, false);
        GuiUtils.showNode(CitizenIvFailure, false);
        GuiUtils.showNode(CPiProgress, true);

        if (request == Request.SUBMIT_CITIZEN_REGISTRATION) {
            CitizenLblStatus.setText(resources.getString("label.submitting"));
        }
        else if (request == Request.CHECK_CITIZEN_REGISTRATION) {
            CitizenLblStatus.setText(resources.getString("label.waitingCitizenRegistration"));
        }

        continueWorkflow();
    }

    @FXML
    public void onStartOverButtonClicked(ActionEvent actionEvent)
    {
        if (citizenRegistrationStatus != null && (citizenRegistrationStatus == Status.SUCCESS || citizenRegistrationStatus == Status.HIT)) {
            startOver();
        }
        else {
            String headerText = Context.getCoreFxController().getResourceBundle()
                    .getString("startingOver.confirmation.header");
            String contentText = Context.getCoreFxController().getResourceBundle()
                    .getString("startingOver.confirmation.message");
            boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);

            if (confirmed) { startOver(); }
        }
    }
}