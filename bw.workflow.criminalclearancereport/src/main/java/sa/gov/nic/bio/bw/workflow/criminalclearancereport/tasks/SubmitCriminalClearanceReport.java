package sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans.CriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.webservice.CriminalClearanceAPI;

import java.time.LocalDate;
import java.util.HashMap;

public class SubmitCriminalClearanceReport extends WorkflowTask {
    @Input(alwaysRequired = true) private CriminalClearanceReport criminalClearanceReport;
    @Output private HashMap<String, Object> criminalClearanceResponse;

    @Override
    public void execute() throws Signal {
        var criminalClearanceAPI = Context.getWebserviceManager().getApi(CriminalClearanceAPI.class);
        var apiCall = criminalClearanceAPI.submitNonCriminalRecord(workflowId, workflowTcn, AppUtils.toJson(criminalClearanceReport));
//        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
        //no fingers found for this id
        //        if(!taskResponse.isSuccess() && "B003-0007".equals(taskResponse.getErrorCode())) return;

//        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
//        criminalClearanceResponse = taskResponse.getResult();
        criminalClearanceResponse=new HashMap<>();
        criminalClearanceResponse.put("reportNumber",1111);
        criminalClearanceResponse.put("expireDate", LocalDate.now());

    }
}
