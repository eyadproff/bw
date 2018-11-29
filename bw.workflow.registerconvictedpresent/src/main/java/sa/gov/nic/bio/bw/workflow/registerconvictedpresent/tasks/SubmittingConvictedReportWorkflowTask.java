package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.ConvictedReportResponse;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class SubmittingConvictedReportWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private ConvictedReport convictedReport;
	@Output private ConvictedReportResponse convictedReportResponse;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn) throws Signal
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		String convictedReportJson = AppUtils.toJson(convictedReport);
		Call<ConvictedReportResponse> apiCall = convictedReportAPI.submitConvictedReport(workflowId, workflowTcn,
		                                                                                 convictedReportJson);
		TaskResponse<ConvictedReportResponse> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		convictedReportResponse = taskResponse.getResult();
	}
}