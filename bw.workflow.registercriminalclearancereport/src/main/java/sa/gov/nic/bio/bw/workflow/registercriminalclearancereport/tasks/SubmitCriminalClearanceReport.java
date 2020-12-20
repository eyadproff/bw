package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans.CriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.webservice.CriminalClearanceAPI;

import java.util.HashMap;

public class SubmitCriminalClearanceReport extends WorkflowTask {
    @Input(alwaysRequired = true) private CriminalClearanceReport criminalClearanceReport;

    // backend send date as String !!
    @Output private HashMap<String, String> criminalClearanceResponse;

    @Override
    public void execute() throws Signal {
        var criminalClearanceAPI = Context.getWebserviceManager().getApi(CriminalClearanceAPI.class);
        var apiCall = criminalClearanceAPI.submitNonCriminalRecord(workflowId, workflowTcn, AppUtils.toJson(criminalClearanceReport));
        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);

        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
        criminalClearanceResponse = taskResponse.getResult();


    }
}
