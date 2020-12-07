package sa.gov.nic.bio.bw.workflow.commons.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.webservice.PassportInfoAPI;

import java.util.Map;

public class GetPassportIdByIdWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true) private long personId;
    @Input private Boolean returnNullResultInCaseNotFound;
    @Output private Map<String,Object> passportNumberAndNationality;

    @Override
    public void execute() throws Throwable {

        PassportInfoAPI passportInfoAPI = Context.getWebserviceManager().getApi(PassportInfoAPI.class);
//        Call<Map<String,Object>> apiCall = passportInfoAPI.retrievePassportNumberAndNationalityBySamisId(workflowId, workflowTcn, personId);
//        TaskResponse<Map<String,Object>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
//
////        boolean notFound = !taskResponse.isSuccess() && "B004-00002".equals(taskResponse.getErrorCode());
//
////        if(returnNullResultInCaseNotFound != null && returnNullResultInCaseNotFound && notFound) return;
//
//        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
//        passportNumberAndNationality = taskResponse.getResult();
//        passportId=1110000L;

    }
}
