package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.utils.CitizenEnrollmentErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

public class IsEnrolledWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true)
    private PersonInfo personInfo;


    @Override
    public void execute() throws Signal {

        if (personInfo.getIsEnrolled().equals("Y")) {
            resetWorkflowStepIfNegativeOrNullTaskResponse(
                    TaskResponse.failure(CitizenEnrollmentErrorCodes.B018_00001.getCode()));
        }
    }
}