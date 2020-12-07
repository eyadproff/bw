package sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks;

import com.google.gson.GsonBuilder;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans.CriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.webservice.CriminalClearanceAPI;

import java.util.HashMap;

public class SubmitCriminalClearanceReport extends WorkflowTask {
    @Input(alwaysRequired = true) private CriminalClearanceReport criminalClearanceReport;

    // backend send date as String !!
    @Output private HashMap<String, String> criminalClearanceResponse;

    @Override
    public void execute() throws Signal {
        var criminalClearanceAPI = Context.getWebserviceManager().getApi(CriminalClearanceAPI.class);
        // We send date as backend (MW) request yyyy-MM-dd
        var apiCall = criminalClearanceAPI.submitNonCriminalRecord(workflowId, workflowTcn, new GsonBuilder().setDateFormat("yyyy-MM-dd").create().toJson(criminalClearanceReport));
        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);

        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
        criminalClearanceResponse = taskResponse.getResult();


    }
}
