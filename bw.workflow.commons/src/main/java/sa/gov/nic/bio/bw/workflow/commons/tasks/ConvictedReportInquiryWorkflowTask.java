package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class ConvictedReportInquiryWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long criminalBiometricsId;
	@Output private List<ConvictedReport> convictedReports;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn) throws Signal
	{
		ConvictedReportInquiryAPI api = Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<List<ConvictedReport>> call = api.inquireConvictedReportByGeneralFileNumber(workflowId, workflowTcn,
		                                                                                 criminalBiometricsId);
		TaskResponse<List<ConvictedReport>> taskResponse = Context.getWebserviceManager().executeApi(call);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		convictedReports = taskResponse.getResult();
	}
}