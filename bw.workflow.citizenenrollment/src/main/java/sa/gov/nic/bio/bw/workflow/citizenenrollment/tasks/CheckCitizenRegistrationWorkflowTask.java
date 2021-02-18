package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.CitizenEnrollmentAPI;

public class CheckCitizenRegistrationWorkflowTask extends WorkflowTask {
    public enum Status {
        PENDING,
        SUCCESS,
        HIT,
        ERROR,
        ENROLLED_HIT
    }

    @Input(alwaysRequired = true)
    private Long personId;
    @Output
    private Status status;


    @Override
    public void execute() throws Signal {
        var api = Context.getWebserviceManager().getApi(CitizenEnrollmentAPI.class);
        var apiCall = api.checkCitizenRegistration(workflowId, workflowTcn, personId);
        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
        resetWorkflowStepIfNegativeTaskResponse(taskResponse);

        Integer Code = taskResponse.getResult();
        if (Code == 1) {
            status = Status.SUCCESS;
        }
        else if (Code == 2) {
            status = Status.PENDING;
        } // Illegal Hit
        else if (Code == 3) {
            status = Status.HIT;
        }
        else if (Code == 4) {
            status = Status.ERROR;
        } // legal Hit
        else if (Code == 7) {
            status = Status.ENROLLED_HIT;
        }


    }
}