package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.WatchListRecord;
import sa.gov.nic.bio.bw.workflow.commons.webservice.CheckCWLAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class CheckCWLByBioIdWorkflowTask extends WorkflowTask {

    @Input private Long bioId;
    @Output private List<WatchListRecord> watchListRecordList;

    @Override
    public void execute() throws Throwable {

        CheckCWLAPI checkCWLAPI = Context.getWebserviceManager().getApi(CheckCWLAPI.class);
        Call<List<WatchListRecord>> apiCall = checkCWLAPI.checkCWLByBioId(workflowId, workflowTcn, bioId);
        TaskResponse<List<WatchListRecord>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
//
        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
        watchListRecordList = taskResponse.getResult();

    }
}
