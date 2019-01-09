package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class ExchangeConvictedReportWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private ConvictedReport convictedReport;
	
	@Override
	public void execute() throws Signal
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		String convictedReportJson = AppUtils.toJson(convictedReport);
		Call<Void> apiCall = convictedReportAPI.exchangeConvictedReport(workflowId, workflowTcn, convictedReportJson);
		TaskResponse<Void> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeTaskResponse(taskResponse);
	}
}