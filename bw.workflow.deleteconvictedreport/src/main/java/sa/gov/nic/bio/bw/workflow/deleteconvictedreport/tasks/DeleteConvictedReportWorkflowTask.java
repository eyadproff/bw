package sa.gov.nic.bio.bw.workflow.deleteconvictedreport.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class DeleteConvictedReportWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long reportNumber;
	
	@Override
	public void execute() throws Signal
	{
		ConvictedReportInquiryAPI api = Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<ConvictedReport> call = api.inquireConvictedReportByReportNumber(workflowId, workflowTcn, reportNumber);
		TaskResponse<ConvictedReport> taskResponse = Context.getWebserviceManager().executeApi(call);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
	}
}