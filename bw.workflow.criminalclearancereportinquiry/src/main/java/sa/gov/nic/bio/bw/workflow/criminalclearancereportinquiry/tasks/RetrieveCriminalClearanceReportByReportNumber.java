package sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans.CriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.webservice.CriminalClearanceAPI;

public class RetrieveCriminalClearanceReportByReportNumber extends WorkflowTask {
    @Input(alwaysRequired = true) private Long reportNumber;

    @Output private CriminalClearanceReport criminalClearanceReport;

    @Override
    public void execute() throws Signal {
        var criminalClearanceAPI = Context.getWebserviceManager().getApi(CriminalClearanceAPI.class);
        var apiCall = criminalClearanceAPI.getNonCriminalRecordByReportNumber(workflowId, workflowTcn, reportNumber);
        var taskResponse = Context.getWebserviceManager().executeApi(apiCall);

        if(!taskResponse.isSuccess() && taskResponse.getErrorCode().equals("B003-0090")) return;

        resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
        criminalClearanceReport = taskResponse.getResult();


    }
}
