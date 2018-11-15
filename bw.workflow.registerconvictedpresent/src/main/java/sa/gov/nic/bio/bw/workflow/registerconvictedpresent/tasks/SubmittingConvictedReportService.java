package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class SubmittingConvictedReportService
{
	public static TaskResponse<ConvictedReportResponse> execute(ConvictedReport convictedReport)
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		String convictedReportJson = AppUtils.toJson(convictedReport);
		Call<ConvictedReportResponse> apiCall = convictedReportAPI.submitConvictedReport(convictedReportJson);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}