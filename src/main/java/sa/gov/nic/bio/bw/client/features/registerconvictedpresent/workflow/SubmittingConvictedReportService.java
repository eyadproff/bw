package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class SubmittingConvictedReportService
{
	public static ServiceResponse<Long> execute(ConvictedReport convictedReport)
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.submitConvictedReport");
		
		String convictedReportJson = new Gson().toJson(convictedReport, TypeToken.get(ConvictedReport.class).getType());
		
		Call<Long> apiCall = convictedReportAPI.submitConvictedReport(url, convictedReportJson);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}