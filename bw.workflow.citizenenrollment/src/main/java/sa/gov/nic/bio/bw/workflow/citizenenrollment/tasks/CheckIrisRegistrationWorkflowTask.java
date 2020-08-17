package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.utils.CitizenEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.IrisRegistrationAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class CheckIrisRegistrationWorkflowTask extends WorkflowTask {
    public enum Status {
        PENDING,
        SUCCESS
    }

    @Input(alwaysRequired = true)
    private Long tcn;

    @Output
    private Status status;

    @Override
    public void execute() throws Signal {
        var api = Context.getWebserviceManager().getApi(IrisRegistrationAPI.class);
        var apiCall = api.checkIrisRegistration(workflowId, workflowTcn, tcn);
        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
        resetWorkflowStepIfNegativeTaskResponse(taskResponse);

        Integer httpCode = taskResponse.getHttpCode();
        if (httpCode == 200) {
            status = Status.SUCCESS;
        }
        else if (httpCode == 202) {
            status = Status.PENDING;
        }


//        resetWorkflowStepIfNegativeOrNullTaskResponse(
//                TaskResponse.failure(CitizenEnrollmentErrorCodes.B018_00001.getCode()));
    }
}