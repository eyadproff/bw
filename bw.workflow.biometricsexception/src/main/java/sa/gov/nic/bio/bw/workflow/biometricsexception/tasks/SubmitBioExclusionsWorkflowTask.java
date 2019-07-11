package sa.gov.nic.bio.bw.workflow.biometricsexception.tasks;


import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.webservice.BioExclusionAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class SubmitBioExclusionsWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true)
    private List<BioExclusion> EditedBioExclusionsList;
    @Output
    private Long Tcn;

    @Override
    public void execute() throws Signal {
        BioExclusionAPI bioExclusionAPI = Context.getWebserviceManager().getApi(BioExclusionAPI.class);
        Call<Long> apiCall = bioExclusionAPI.submitBioExclusions(AppUtils.toJson(EditedBioExclusionsList));
        TaskResponse<Long> taskResponse = Context.getWebserviceManager().executeApi(apiCall);

        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);

        Tcn = taskResponse.getResult();
    }
}
