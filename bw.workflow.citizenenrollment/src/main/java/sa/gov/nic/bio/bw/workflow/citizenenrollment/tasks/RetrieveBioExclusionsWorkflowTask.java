package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;


import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.CitizenEnrollmentAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;


public class RetrieveBioExclusionsWorkflowTask extends WorkflowTask {


    @Input(alwaysRequired = true)
    private Integer samisId;

    @Output
    private List<BioExclusion> bioExclusionList;


    @Override
    public void execute() throws Signal {

        CitizenEnrollmentAPI bioExclusionAPI = Context.getWebserviceManager().getApi(CitizenEnrollmentAPI.class);
        Call<List<BioExclusion>> apiCall = bioExclusionAPI.retrieveBioExclusions(workflowId, workflowTcn, samisId);
        TaskResponse<List<BioExclusion>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);


        boolean notFound = !taskResponse.isSuccess() && "B003-0079".equals(taskResponse.getErrorCode());

        if (notFound) {
            return;
        }

        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
        bioExclusionList = taskResponse.getResult();


    }

}