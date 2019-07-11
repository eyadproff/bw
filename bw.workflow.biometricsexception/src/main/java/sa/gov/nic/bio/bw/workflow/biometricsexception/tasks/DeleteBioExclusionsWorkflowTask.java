package sa.gov.nic.bio.bw.workflow.biometricsexception.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.webservice.BioExclusionAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class DeleteBioExclusionsWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true)
    private List<Integer> SeqNumbersList;
    @Output
    private Long Tcn;

    @Override
    public void execute() throws Signal {
        UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
        BioExclusionAPI bioExclusionAPI = Context.getWebserviceManager().getApi(BioExclusionAPI.class);
        Call<Long> apiCall = bioExclusionAPI.deleteBioExclusions(SeqNumbersList, userInfo.getOperatorId());
        TaskResponse<Long> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);

        Tcn = taskResponse.getResult();
    }
}
