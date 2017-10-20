package sa.gov.nic.bio.bw.client.home.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RefreshTokenAPI
{
	@FormUrlEncoded
	@POST
	Call<RefreshTokenBean> refreshToken(@Url String url, @Field("token") String token);
}