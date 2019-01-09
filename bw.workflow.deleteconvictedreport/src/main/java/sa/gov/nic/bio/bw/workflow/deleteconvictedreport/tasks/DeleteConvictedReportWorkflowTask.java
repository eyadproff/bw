package sa.gov.nic.bio.bw.workflow.deleteconvictedreport.tasks;

import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

public class DeleteConvictedReportWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long reportNumber;
	
	@Override
	public void execute() throws Signal
	{
		//ConvictedReportInquiryAPI api = Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		//Call<ConvictedReport> call = api.inquireConvictedReportByReportNumber(workflowId, workflowTcn, reportNumber);
		//TaskResponse<ConvictedReport> taskResponse = Context.getWebserviceManager().executeApi(call);
		//resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
	}
}