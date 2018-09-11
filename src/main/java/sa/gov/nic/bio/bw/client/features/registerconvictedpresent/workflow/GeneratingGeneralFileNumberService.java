package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class GeneratingGeneralFileNumberService
{
	public static ServiceResponse<Long> execute(Long personId, Long bioId)
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		Call<Long> apiCall = convictedReportAPI.generateGeneralFileNumber(personId, bioId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}