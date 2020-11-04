package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.PersonInfoByIdAPI;

import sa.gov.nic.bio.commons.TaskResponse;

public class GetPersonInfoByIdWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true)
    private long personId;
    @Input
    private Boolean returnNullResultInCaseNotFound;

    @Output
    private PersonInfo personInfo;

    @Override
    public void execute() throws Signal {
        PersonInfoByIdAPI personInfoByIdAPI = Context.getWebserviceManager().getApi(PersonInfoByIdAPI.class);
        Call<PersonInfo> apiCall = personInfoByIdAPI.getPersonInfoById(workflowId, workflowTcn, personId, 1);
        TaskResponse<PersonInfo> taskResponse = Context.getWebserviceManager().executeApi(apiCall);

        boolean notFound = !taskResponse.isSuccess() && "B004-00002".equals(taskResponse.getErrorCode());

        if (returnNullResultInCaseNotFound != null && returnNullResultInCaseNotFound && notFound) {
            return;
        }

        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);

        personInfo = taskResponse.getResult();
    }
}