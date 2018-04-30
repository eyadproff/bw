package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ConvictedReportAPI
{
	@POST
	Call<Long> generateGeneralFileNumber(@Url String url);
	
	@FormUrlEncoded
	@POST
	Call<Long> submitConvictedReport(@Url String url, @Field("convicted-report") String convictedReport);
}