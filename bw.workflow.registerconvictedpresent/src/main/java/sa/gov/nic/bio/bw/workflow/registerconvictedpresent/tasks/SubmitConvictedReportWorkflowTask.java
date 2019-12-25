package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.ConvictedReportResponse;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalFingerprintSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalWorkflowSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReportAPI;

public class SubmitConvictedReportWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private ConvictedReport convictedReport;
	@Input private CriminalWorkflowSource criminalWorkflowSource;
	@Input private CriminalFingerprintSource criminalFingerprintSource;
	@Output private ConvictedReportResponse convictedReportResponse;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		String convictedReportJson = AppUtils.toJson(convictedReport);
		var apiCall = api.submitConvictedReport(workflowId, workflowTcn, convictedReportJson,
		                                        criminalWorkflowSource, criminalFingerprintSource);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		convictedReportResponse = taskResponse.getResult();
	}
}