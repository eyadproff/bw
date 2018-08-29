package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class GeneratingGeneralFileNumberService
{
	public static ServiceResponse<Long> execute()
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		Call<Long> apiCall = convictedReportAPI.generateGeneralFileNumber();
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}