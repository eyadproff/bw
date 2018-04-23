package sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ConvictedReportAPI
{
	@FormUrlEncoded
	@POST
	Call<Long> submitConvictedReport(@Url String url, @Field("convicted-report") String convictedReport);
}