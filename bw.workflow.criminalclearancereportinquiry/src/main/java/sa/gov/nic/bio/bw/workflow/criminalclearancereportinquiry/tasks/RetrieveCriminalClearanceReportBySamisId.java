package sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans.CriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.webservice.CriminalClearanceAPI;

import java.util.List;

public class RetrieveCriminalClearanceReportBySamisId extends WorkflowTask {
    @Input(alwaysRequired = true) private Long personId;

    @Output private List<CriminalClearanceReport> criminalClearanceReports;

    @Override
    public void execute() throws Signal {
        var criminalClearanceAPI = Context.getWebserviceManager().getApi(CriminalClearanceAPI.class);
        var apiCall = criminalClearanceAPI.getNonCriminalRecordBySamisId(workflowId, workflowTcn, personId);
        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);

        if(!taskResponse.isSuccess() && taskResponse.getErrorCode().equals("B003-0090")) return;

        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
        criminalClearanceReports = taskResponse.getResult();


    }
}
