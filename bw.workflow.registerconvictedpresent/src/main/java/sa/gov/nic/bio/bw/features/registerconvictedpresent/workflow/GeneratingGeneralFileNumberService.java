package sa.gov.nic.bio.bw.features.registerconvictedpresent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class GeneratingGeneralFileNumberService
{
	public static TaskResponse<Long> execute(Long personId, Long bioId)
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		Call<Long> apiCall = convictedReportAPI.generateGeneralFileNumber(personId, bioId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}