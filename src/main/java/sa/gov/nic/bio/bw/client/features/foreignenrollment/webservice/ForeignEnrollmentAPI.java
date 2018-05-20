package sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ForeignEnrollmentAPI
{
	@FormUrlEncoded
	@POST
	Call<Long> enrollForeign(@Url String url, @Field("foreign-info") String foreignInfo);
}