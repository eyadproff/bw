package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

public class SubmitCitizenRegistrationWorkflowTask extends WorkflowTask {
//    @Input(alwaysRequired = true)
//    private CitizenEnrollmentInfo citizenEnrollmentInfo;


    @Output
    private Long tcn;

    @Override
    public void execute() throws Signal {
//        var api = Context.getWebserviceManager().getApi(CitizenEnrollmentAPI.class);
//        var apiCall = api.enrollPerson(workflowId, workflowTcn, citizenEnrollmentInfo.getPersonId(), citizenEnrollmentInfo.getPersonType(),
//                AppUtils.toJson(citizenEnrollmentInfo.getFingers())
//                , AppUtils.toJson(citizenEnrollmentInfo.getMissing()), citizenEnrollmentInfo.getFaceImage(),
//                AppUtils.toJson(citizenEnrollmentInfo.getBirthDate()), citizenEnrollmentInfo.getGender(), null);
//        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
//        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
//        tcn = taskResponse.getResult();
        System.out.println("SubmitCitizen");
    }
}