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
import sa.gov.nic.bio.bw.core.workflow.*;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.CitizenEnrollmentInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.CheckCitizenRegistrationWorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.CheckCitizenRegistrationWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.CitizenRegistrationWorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.utils.CitizenEnrollmentErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Map;

@FxmlFile("registeringCitizen.fxml")
public class RegisteringCitizenPaneFxController extends WizardStepFxControllerBase {
    public enum Request {
        SUBMIT_CITIZEN_REGISTRATION,
        CHECK_CITIZEN_REGISTRATION
    }

    @Input(alwaysRequired = true)
    private CitizenEnrollmentInfo citizenEnrollmentInfo;


    @Output
    private Request request;

    @FXML
    private ProgressIndicator piProgress;
    @FXML
    private ImageView ivSuccess;
    @FXML
    private ImageView ivFailure;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnRetry;
    @FXML
    private Button btnStartOver;

    private Status citizenRegistrationStatus;
    private boolean disableRetryButtonForever = false;

    @Override
    protected void onAttachedToScene() {
        request = Request.SUBMIT_CITIZEN_REGISTRATION;
        RegisteringTasks();
        //continueWorkflow();
        //se data to task

//        executeUiTask(SubmitCitizenRegistrationWorkflowTask.class, new SuccessHandler() {
//            @Override
//            protected void onSuccess() {
//                lblStatus.setText(resources.getString("label.waitingIrisRegistration"));
//                request = Request.CHECK_IRIS_REGISTRATION;
//                continueWorkflow();
//            }
//        }, throwable -> {
//
//            lblStatus.setText(resources.getString("label.failedToSendIris"));
//            piProgress.setVisible(false);
//            ivFailure.setVisible(true);
//            btnStartOver.setVisible(true);
//            btnRetry.setVisible(!disableRetryButtonForever);
//
//            if (throwable instanceof Signal) {
//
//                Signal signal = (Signal) throwable;
//                Map<String, Object> payload = signal.getPayload();
//                if (payload != null) {
//                    TaskResponse<?> taskResponse = (TaskResponse<?>)
//                            payload.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
//
//                    if (taskResponse != null) {
//                        this.reportNegativeTaskResponse(taskResponse.getErrorCode(),
//                                taskResponse.getException(),
//                                taskResponse.getErrorDetails());
//                        System.out.println("test test1");
//                        return;
//                    }
//                }
//
//                String errorCode = CitizenEnrollmentErrorCodes.C011_00001.getCode();
//                String[] errorDetails = {"failed to execute the task SubmitCitizenRegistrationWorkflowTask! signal = " +
//                        signal};
//                Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
//            } else {
//                System.out.println("test test2");
//                String errorCode = CitizenEnrollmentErrorCodes.C011_00002.getCode();
//                String[] errorDetails = {"failed to execute the task SubmitCitizenRegistrationWorkflowTask!"};
//                Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
//            }
//
//        });

    }

    private void RegisteringTasks() {
        //  Request request = getData(RegisteringIrisPaneFxController.class, "request");
        if (request == Request.SUBMIT_CITIZEN_REGISTRATION) {
            //setData(SubmitCitizenRegistrationWorkflowTask.class,"citizenEnrollmentInfo",citizenEnrollmentInfo);
//                    passData(PersonIdPaneFxController.class, SubmitCitizenRegistrationWorkflowTask.class,
//                            "personId");
//                    passData(sa.gov.nic.bio.bw.workflow.commons.controllers.IrisCapturingFxController.class, "capturedRightIrisBase64",
//                            SubmitIrisRegistrationWorkflowTask.class, "rightIrisBase64");
//                    passData(sa.gov.nic.bio.bw.workflow.commons.controllers.IrisCapturingFxController.class, "capturedLeftIrisBase64",
//                            SubmitIrisRegistrationWorkflowTask.class, "leftIrisBase64");
            executeTask(CitizenRegistrationWorkflowTask.class);
        } else if (request == Request.CHECK_CITIZEN_REGISTRATION) {

//                    passData(SubmitCitizenRegistrationWorkflowTask.class,
//                            CheckCitizenRegistrationWorkflowTask.class, "tcn");
            executeTask(CheckCitizenRegistrationWorkflowTask.class);
//                    passData(CheckIrisRegistrationWorkflowTask.class, "status",
//                            RegisteringIrisPaneFxController.class, "irisRegistrationStatus");

        }
    }

    public void onReturnFromExecuteTask(boolean successfulResponse) {
        if (request == Request.SUBMIT_CITIZEN_REGISTRATION) {
            if (successfulResponse) {
                lblStatus.setText(resources.getString("label.waitingCitizenRegistration"));
                request = Request.CHECK_CITIZEN_REGISTRATION;
                RegisteringTasks();
            } else {
                lblStatus.setText(resources.getString("label.failedToSendCitizenInfo"));
                piProgress.setVisible(false);
                ivFailure.setVisible(true);
                btnStartOver.setVisible(true);
                btnRetry.setVisible(!disableRetryButtonForever);
            }
        } else if (request == Request.CHECK_CITIZEN_REGISTRATION) {

            if (successfulResponse) {
                citizenRegistrationStatus = getData(CheckCitizenRegistrationWorkflowTask.class, "status");

                if (citizenRegistrationStatus == Status.PENDING) {

                    Context.getExecutorService().submit(() ->
                    {
                        try {
                            int seconds = Integer.parseInt(Context.getConfigManager().getProperty(
                                    "registerIris.inquiry.checkEverySeconds"));

                            Thread.sleep(seconds * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Platform.runLater(this::RegisteringTasks);
                    });

                } else if (citizenRegistrationStatus == Status.SUCCESS) {
                    lblStatus.setText(resources.getString("label.successCitizenRegistration"));
                    piProgress.setVisible(false);
                    ivSuccess.setVisible(true);
                    btnStartOver.setVisible(true);
                }
            } else {
                lblStatus.setText(resources.getString("label.failedToRegisterCitizen"));
                piProgress.setVisible(false);
                ivFailure.setVisible(true);
                btnStartOver.setVisible(true);
                btnRetry.setVisible(!disableRetryButtonForever);

            }
        }
    }

    @Override
    public void reportNegativeTaskResponse(String errorCode, Throwable exception, String[] errorDetails) {
        if ("B003-0066".equals(errorCode)) disableRetryButtonForever = true;

        super.reportNegativeTaskResponse(errorCode, exception, errorDetails);
    }

    @FXML
    private void onRetryButtonClicked(ActionEvent actionEvent) {
        btnRetry.setVisible(false);
        btnStartOver.setVisible(false);
        ivFailure.setVisible(false);
        piProgress.setVisible(true);

        if (request == Request.SUBMIT_CITIZEN_REGISTRATION) {
            lblStatus.setText(resources.getString("label.submitting"));
        } else if (request == Request.CHECK_CITIZEN_REGISTRATION) {
            lblStatus.setText(resources.getString("label.waitingCitizenRegistration"));
        }

        RegisteringTasks();
    }


    private boolean executeTask(Class<? extends WorkflowTask> taskClass) {

        return executeUiTask(taskClass, new SuccessHandler() {
                    @Override
                    protected void onSuccess() {
//                        if (taskClass.equals(SubmitCitizenRegistrationWorkflowTask.class)) {
//							lblStatus.setText(resources.getString("label.waitingIrisRegistration"));
//							request = Request.CHECK_IRIS_REGISTRATION;
//
//                        } else if (taskClass.equals(CheckCitizenRegistrationWorkflowTask.class)) {
//                            if (irisRegistrationStatus == Status.PENDING) {
//                                Context.getExecutorService().submit(() ->
//                                {
//                                    try {
//                                        int seconds = Integer.parseInt(Context.getConfigManager().getProperty(
//                                                "registerIris.inquiry.checkEverySeconds"));
//                                        Thread.sleep(seconds * 1000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    Platform.runLater(this::continueWorkflow);
//
//
//                                });
//
//                            } else if (irisRegistrationStatus == Status.SUCCESS) {
//                                lblStatus.setText(resources.getString("label.successIrisRegistration"));
//                                piProgress.setVisible(false);
//                                ivSuccess.setVisible(true);
//                                btnStartOver.setVisible(true);
//
//                            }
//
//                        }
                        onReturnFromExecuteTask(true);

                    }
                }, throwable ->
                {

                    onReturnFromExecuteTask(false);

                    if (throwable instanceof Signal) {
                        Signal signal = (Signal) throwable;
                        Map<String, Object> payload = signal.getPayload();
                        if (payload != null) {
                            TaskResponse<?> taskResponse = (TaskResponse<?>)
                                    payload.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);

                            if (taskResponse != null) {
                                this.reportNegativeTaskResponse(taskResponse.getErrorCode(),
                                        taskResponse.getException(),
                                        taskResponse.getErrorDetails());
                                return;
                            }
                        }

                        String errorCode = CitizenEnrollmentErrorCodes.C011_00010.getCode();
                        String[] errorDetails = {"failed to execute the task " + taskClass.getName() + "! signal = " +
                                signal};
                        Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
                    } else {
                        String errorCode = CitizenEnrollmentErrorCodes.C011_00011.getCode();
                        String[] errorDetails = {"failed to execute the task " + taskClass.getName() + "!"};
                        Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
                    }

                }
        );


    }
}