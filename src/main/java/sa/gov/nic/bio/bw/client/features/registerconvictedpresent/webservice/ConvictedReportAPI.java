package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportResponse;

public interface ConvictedReportAPI
{
	@POST
	Call<Long> generateGeneralFileNumber(@Url String url);
	
	@FormUrlEncoded
	@POST
	Call<ConvictedReportResponse> submitConvictedReport(@Url String url,
	                                                    @Field("convicted-report") String convictedReport);
}