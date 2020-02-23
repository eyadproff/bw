package sa.gov.nic.bio.bw.workflow.biometricsexception.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.webservice.BioExclusionAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class RetrieveBioExclusionsWorkflowTask extends WorkflowTask {

    @Input(alwaysRequired = true)
    private Integer samisId;
    @Output
    private List<BioExclusion> bioExclusionList;

    @Override
    public void execute() throws Signal {
        BioExclusionAPI bioExclusionAPI = Context.getWebserviceManager().getApi(BioExclusionAPI.class);
        Call<List<BioExclusion>> apiCall = bioExclusionAPI.retrieveBioExclusions(workflowId, workflowTcn, samisId);
        TaskResponse<List<BioExclusion>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
        boolean notFound = !taskResponse.isSuccess() && "B003-0079".equals(taskResponse.getErrorCode());

        if (notFound) return;

        //should return which one Expired
            if ("B003-0079".equals(taskResponse.getErrorCode()))
                System.out.println("Bio Exclusion period expired");
            else
                resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);


        bioExclusionList = taskResponse.getResult();

    }
}
