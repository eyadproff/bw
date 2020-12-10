package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.webservice.PassportInfoAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Map;

public class GetPassportIdByIdWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true) private long personId;
    @Input private Boolean returnNullResultInCaseNotFound;
    @Output private Map<String, Object> passportNumberAndNationality;
    @Output private String passportId;

    @Override
    public void execute() throws Throwable {

        PassportInfoAPI passportInfoAPI = Context.getWebserviceManager().getApi(PassportInfoAPI.class);
        Call<Map<String, Object>> apiCall = passportInfoAPI.retrievePassportNumberAndNationalityBySamisId(workflowId, workflowTcn, personId);
        TaskResponse<Map<String, Object>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);

        boolean notFound = !taskResponse.isSuccess() && "B004-0037".equals(taskResponse.getErrorCode());

        if (returnNullResultInCaseNotFound != null && returnNullResultInCaseNotFound && notFound) { return; }

        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
        passportNumberAndNationality = taskResponse.getResult();
        passportId = (String) passportNumberAndNationality.get("passport-number");

    }
}
