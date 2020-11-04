package sa.gov.nic.bio.bw.workflow.biometricsexception.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.SubmissionAndDeletionResponse;
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
        Call<SubmissionAndDeletionResponse> apiCall = bioExclusionAPI
                .deleteBioExclusions(workflowId, workflowTcn, AppUtils.toJson(SeqNumbersList),
                        userInfo.getOperatorId());
        TaskResponse<SubmissionAndDeletionResponse> taskResponse = Context.getWebserviceManager().executeApi(apiCall);

        boolean notFound = !taskResponse.isSuccess() && "B003-0078".equals(taskResponse.getErrorCode());

        if (notFound) { return; }


        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);

        Tcn = taskResponse.getResult().getTcn();
    }
}
