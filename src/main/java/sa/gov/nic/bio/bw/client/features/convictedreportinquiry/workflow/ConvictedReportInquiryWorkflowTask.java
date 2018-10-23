package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class ConvictedReportInquiryWorkflowTask implements WorkflowTask
{
	@Input(required = true) private long generalFileNumber;
	@Output private List<ConvictedReport> convictedReports;
	
	@Override
	public void execute() throws Signal
	{
		ConvictedReportInquiryAPI convictedReportInquiryAPI =
												Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<List<ConvictedReport>> apiCall =
								convictedReportInquiryAPI.inquireConvictedReportByGeneralFileNumber(generalFileNumber);
		TaskResponse<List<ConvictedReport>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		convictedReports = taskResponse.getResult();
	}
}