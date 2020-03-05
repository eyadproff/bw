package sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.Workflow;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.IsSameGenderWorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.utils.CitizenEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Map;

@FxmlFile("personId.fxml")
public class PersonIdPaneFxController extends WizardStepFxControllerBase {
    @Output
    private Long personId;
    @Output
    private PersonInfo personInfo;

    @FXML
    private ProgressIndicator piProgress;
    @FXML
    private TextField txtPersonId;
    @FXML
    private Button btnNext;

    @Override
    protected void onAttachedToScene() {
        GuiUtils.applyValidatorToTextField(txtPersonId, "^1\\d*", "\\D+|^[02-9]",
                10);

        btnNext.disableProperty().bind(txtPersonId.textProperty().isEmpty().or(txtPersonId.disabledProperty()));
        btnNext.setOnAction(actionEvent ->
        {
            personId = Long.parseLong(txtPersonId.getText());
            onNextButtonClicked(actionEvent);
        });

        if (personId != null) txtPersonId.setText(String.valueOf(personId));
        txtPersonId.requestFocus();
    }


//    @Override
//    public void onReturnFromWorkflow(boolean successfulResponse) {
//        if (successfulResponse) goNext();
//    }

    @Override
    public void onShowingProgress(boolean bShow) {
        piProgress.setVisible(bShow);
        txtPersonId.setDisable(bShow);
    }

    @FXML
    private void onEnterPressed(ActionEvent actionEvent) {
        btnNext.fire();
    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
//        setData(GetPersonInfoByIdWorkflowTask.class,
//                "personId", personId);
//
////        if(executeTask(GetPersonInfoByIdWorkflowTask.class)){
////            passData(GetPersonInfoByIdWorkflowTask.class,IsSameGenderWorkflowTask.class,
////                    "personInfo");
////            if( executeTask(IsSameGenderWorkflowTask.class)){
////                personInfo=getData(GetPersonInfoByIdWorkflowTask.class,"personInfo");
////                goNext();
////            }
////        }
//
//
//        executeUiTask(GetPersonInfoByIdWorkflowTask.class, new SuccessHandler() {
//                    @Override
//                    protected void onSuccess() {
//                        setData(IsSameGenderWorkflowTask.class,
//                                "personInfo", getData("personInfo"));
//                        personInfo = getData( "personInfo");
//                        executeTask(IsSameGenderWorkflowTask.class);
//                    }
//                }, throwable ->
//                {
//
//
//                    if (throwable instanceof Signal) {
//
//                        Signal signal = (Signal) throwable;
//                        Map<String, Object> payload = signal.getPayload();
//                        if (payload != null) {
//                            TaskResponse<?> taskResponse = (TaskResponse<?>)
//                                    payload.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
//
//                            if (taskResponse != null) {
//                                this.reportNegativeTaskResponse(taskResponse.getErrorCode(),
//                                        taskResponse.getException(),
//                                        taskResponse.getErrorDetails());
//                                System.out.println("test test1");
//                                return ;
//                            }
//                        }
//
//						String errorCode = CitizenEnrollmentErrorCodes.C011_00001.getCode();
//						String[] errorDetails = {"failed to execute the task GetPersonInfoByIdWorkflowTask! signal = " +
//								signal};
//						Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
//                    } else {
//                        System.out.println("test test2");
//						String errorCode = CitizenEnrollmentErrorCodes.C011_00002.getCode();
//						String[] errorDetails = {"failed to execute the task GetPersonInfoByIdWorkflowTask!"};
//						Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
//                    }
//                }
//        );
        // continueWorkflow();
        preparationForTasks();

    }

    private void preparationForTasks() {
        setData(GetPersonInfoByIdWorkflowTask.class,
                "personId", personId);

        executeTask(GetPersonInfoByIdWorkflowTask.class) ;

    }

    private boolean executeTask(Class<? extends WorkflowTask> taskClass) {

        return executeUiTask(taskClass, new SuccessHandler() {
                    @Override
                    protected void onSuccess() {
                        if (taskClass.getName().equals(GetPersonInfoByIdWorkflowTask.class.getName())) {
                            setData(IsSameGenderWorkflowTask.class,
                                    "personInfo", getData("personInfo"));
                            personInfo = getData("personInfo");
                            executeTask(IsSameGenderWorkflowTask.class);
                        }
                        if (taskClass.getName().equals(IsSameGenderWorkflowTask.class.getName())) {
                            goNext();
                        }
                    }
                }, throwable ->
                {


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

                        String errorCode = CitizenEnrollmentErrorCodes.C011_00001.getCode();
                        String[] errorDetails = {"failed to execute the task " + taskClass.getName() + "! signal = " +
                                signal};
                        Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
                    } else {
                        String errorCode = CitizenEnrollmentErrorCodes.C011_00002.getCode();
                        String[] errorDetails = {"failed to execute the task " + taskClass.getName() + "!"};
                        Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
                    }
                }
        );


    }
}