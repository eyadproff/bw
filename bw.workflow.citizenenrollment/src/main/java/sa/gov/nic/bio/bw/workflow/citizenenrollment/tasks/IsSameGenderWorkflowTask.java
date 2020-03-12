package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.utils.CitizenEnrollmentErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

public class IsSameGenderWorkflowTask extends WorkflowTask
{
   @Input(alwaysRequired = true) private PersonInfo personInfo;


    @Override
    public void execute() throws Signal
    {
        UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");

               if (personInfo.getGender() != userInfo.getGender()) {
                    String[] x={"The Operator and the Citizen must be the same gender"};
                    resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(CitizenEnrollmentErrorCodes.B018_00003.getCode(),x));
                }
    }
}