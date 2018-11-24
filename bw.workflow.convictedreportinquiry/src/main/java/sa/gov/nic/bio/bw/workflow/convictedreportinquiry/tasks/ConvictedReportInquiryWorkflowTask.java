package sa.gov.nic.bio.bw.workflow.convictedreportinquiry.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.convictedreportinquiry.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class ConvictedReportInquiryWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long generalFileNumber;
	@Output private List<ConvictedReport> convictedReports;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn) throws Signal, InterruptedException
	{
		ConvictedReportInquiryAPI convictedReportInquiryAPI =
												Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<List<ConvictedReport>> apiCall =
								convictedReportInquiryAPI.inquireConvictedReportByGeneralFileNumber(workflowId,
								                                                                    workflowTcn,
								                                                                    generalFileNumber);
		TaskResponse<List<ConvictedReport>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		convictedReports = taskResponse.getResult();
	}
}