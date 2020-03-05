package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.CitizenEnrollmentAPI;

public class SubmitCitizenRegistrationWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true)
    private Long personId;
    @Input(alwaysRequired = true)
    private Integer personType;
    @Input(alwaysRequired = true)
    private String fingers;
    @Input(alwaysRequired = true)
    private String missing;
    @Input(alwaysRequired = true)
    private String faceImage;
    @Input(alwaysRequired = true)
    private String birthDate;
    @Input(alwaysRequired = true)
    private Integer gender;

    @Output
    private Long tcn;

    @Override
    public void execute() throws Signal {
        var api = Context.getWebserviceManager().getApi(CitizenEnrollmentAPI.class);
        var apiCall = api.enrollPerson(workflowId, workflowTcn, personId, personType, fingers, missing, faceImage, birthDate, gender, null);
        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
        tcn = taskResponse.getResult();
    }
}