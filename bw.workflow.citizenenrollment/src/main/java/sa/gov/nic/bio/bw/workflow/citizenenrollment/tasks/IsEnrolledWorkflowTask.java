package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.utils.CitizenEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.CitizenEnrollmentAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class IsEnrolledWorkflowTask extends WorkflowTask {

    @Input(alwaysRequired = true)
    private Long personId;
    @Output
    private Boolean isEnrolled;


    @Override
    public void execute() throws Signal {

        var api = Context.getWebserviceManager().getApi(CitizenEnrollmentAPI.class);
        var apiCall = api.checkCitizenRegistration(workflowId, workflowTcn, personId);
        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
        resetWorkflowStepIfNegativeTaskResponse(taskResponse);

        Integer code = taskResponse.getResult();
        if (code == 1 || code == 3 || code == 5 || code == 7 || code == 8) {
            isEnrolled = true;
            resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(CitizenEnrollmentErrorCodes.B018_00001.getCode()));
        }
        else if (code == 2 || code == 22) {
            resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(CitizenEnrollmentErrorCodes.B018_00004.getCode()));
        }
        else { isEnrolled = false; }

    }

}