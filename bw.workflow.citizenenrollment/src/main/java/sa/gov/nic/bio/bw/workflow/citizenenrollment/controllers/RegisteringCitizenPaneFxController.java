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
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.CheckCitizenRegistrationWorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.CheckIrisRegistrationWorkflowTask;

@FxmlFile("registeringCitizen.fxml")
public class RegisteringCitizenPaneFxController extends WizardStepFxControllerBase {
    public enum Request {
        SUBMIT_CITIZEN_REGISTRATION,
        CHECK_CITIZEN_REGISTRATION,
        SUBMIT_IRIS_REGISTRATION,
        CHECK_IRIS_REGISTRATION
    }

    @Input
    private CheckCitizenRegistrationWorkflowTask.Status citizenRegistrationStatus;
    @Input
    private CheckIrisRegistrationWorkflowTask.Status irisRegistrationStatus;

    @Input(alwaysRequired = true)
    Boolean skipIris;

    @Output
    private Request request;

    @FXML
    private ProgressIndicator CPiProgress;
    @FXML
    private ImageView CitizenIvSuccess;
    @FXML
    private ImageView CitizenIvFailure;
    @FXML
    private Label CitizenLblStatus;

    @FXML
    private ProgressIndicator IPiProgress;
    @FXML
    private ImageView IrisIvSuccess;
    @FXML
    private ImageView IrisIvFailure;
    @FXML
    private Label IrisLblStatus;

    @FXML
    private Button btnRetry;
    @FXML
    private Button btnStartOver;


    private boolean disableRetryButtonForever = false;


    @Override
    protected void onAttachedToScene() {
        request = Request.SUBMIT_CITIZEN_REGISTRATION;
        continueWorkflow();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (request == Request.SUBMIT_CITIZEN_REGISTRATION) {
            if (successfulResponse) {
                CitizenLblStatus.setText(resources.getString("label.waitingCitizenRegistration"));
                request = Request.CHECK_CITIZEN_REGISTRATION;
                continueWorkflow();
            }
            else {
                CitizenLblStatus.setText(resources.getString("label.failedToSendCitizenInfo"));
                CPiProgress.setVisible(false);
                CitizenIvFailure.setVisible(true);
                btnStartOver.setVisible(true);
                btnRetry.setVisible(!disableRetryButtonForever);
            }
        }
        else if (request == Request.CHECK_CITIZEN_REGISTRATION) {
            if (successfulResponse) {
                if (citizenRegistrationStatus == CheckCitizenRegistrationWorkflowTask.Status.PENDING) {
                    Context.getExecutorService().submit(() -> {
                        try {
                            int seconds = Integer.parseInt(Context.getConfigManager().getProperty(
                                    "registerCitizen.inquiry.checkEverySeconds"));
                            Thread.sleep(seconds * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Platform.runLater(this::continueWorkflow);
                    });

                }
                else if (citizenRegistrationStatus == CheckCitizenRegistrationWorkflowTask.Status.SUCCESS) {
                    CitizenLblStatus.setText(resources.getString("label.successCitizenRegistration"));
                    CPiProgress.setVisible(false);
                    CitizenIvSuccess.setVisible(true);


                    if (skipIris) {
                        btnStartOver.setVisible(true);
                    }
                    else {
                        IPiProgress.setVisible(true);
                        IrisLblStatus.setVisible(true);
                        //  IrisLblStatus.setText(resources.getString("label.submittingIris"));
                        request = Request.SUBMIT_IRIS_REGISTRATION;
                        continueWorkflow();
                    }
                }
            }
            else {
                CitizenLblStatus.setText(resources.getString("label.failedToRegisterCitizen"));
                CPiProgress.setVisible(false);
                CitizenIvFailure.setVisible(true);
                btnStartOver.setVisible(true);
                btnRetry.setVisible(!disableRetryButtonForever);
            }
        }
        else if (request == Request.SUBMIT_IRIS_REGISTRATION) {
            if (successfulResponse) {
                IrisLblStatus.setText(resources.getString("label.waitingIrisRegistration"));
                request = Request.CHECK_IRIS_REGISTRATION;
                continueWorkflow();
            }
            else {
                IrisLblStatus.setText(resources.getString("label.failedToSendIris"));
                IPiProgress.setVisible(false);
                IrisIvFailure.setVisible(true);
                btnStartOver.setVisible(true);
                btnRetry.setVisible(!disableRetryButtonForever);
            }
        }
        else if (request == Request.CHECK_IRIS_REGISTRATION) {
            if (successfulResponse) {
                if (irisRegistrationStatus == CheckIrisRegistrationWorkflowTask.Status.PENDING) {
                    Context.getExecutorService().submit(() -> {
                        try {
                            int seconds = Integer.parseInt(
                                    Context.getConfigManager().getProperty("registerIris.inquiry.checkEverySeconds"));
                            Thread.sleep(seconds * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Platform.runLater(this::continueWorkflow);
                    });

                }
                else if (irisRegistrationStatus == CheckIrisRegistrationWorkflowTask.Status.SUCCESS) {
                    IrisLblStatus.setText(resources.getString("label.successIrisRegistration"));
                    IPiProgress.setVisible(false);
                    IrisIvSuccess.setVisible(true);
                    btnStartOver.setVisible(true);
                }
            }
            else {
                IrisLblStatus.setText(resources.getString("label.failedToRegisterIris"));
                IPiProgress.setVisible(false);
                IrisIvFailure.setVisible(true);
                btnStartOver.setVisible(true);
                btnRetry.setVisible(!disableRetryButtonForever);
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
        btnRetry.setVisible(false);
        btnStartOver.setVisible(false);


        if (request == Request.SUBMIT_CITIZEN_REGISTRATION) {
            CitizenLblStatus.setText(resources.getString("label.submitting"));
            CitizenIvFailure.setVisible(false);
            CPiProgress.setVisible(true);
        }
        else if (request == Request.CHECK_CITIZEN_REGISTRATION) {
            CitizenLblStatus.setText(resources.getString("label.waitingCitizenRegistration"));
            CitizenIvFailure.setVisible(false);
            CPiProgress.setVisible(true);
        }
        else if (request == Request.SUBMIT_IRIS_REGISTRATION) {
            IrisLblStatus.setText(resources.getString("label.submittingIris"));
            IrisIvFailure.setVisible(false);
            IPiProgress.setVisible(true);
        }
        else if (request == Request.CHECK_IRIS_REGISTRATION) {
            IrisLblStatus.setText(resources.getString("label.waitingIrisRegistration"));
            IrisIvFailure.setVisible(false);
            IPiProgress.setVisible(true);
        }


        continueWorkflow();
    }
}