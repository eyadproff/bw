package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class SubmittingConvictedReportService
{
	public static TaskResponse<ConvictedReportResponse> execute(ConvictedReport convictedReport)
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		String convictedReportJson = new Gson().toJson(convictedReport, TypeToken.get(ConvictedReport.class).getType());
		Call<ConvictedReportResponse> apiCall = convictedReportAPI.submitConvictedReport(convictedReportJson);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}