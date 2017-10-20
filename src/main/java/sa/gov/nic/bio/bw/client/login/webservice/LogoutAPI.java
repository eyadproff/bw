package sa.gov.nic.bio.bw.client.login.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

public interface LogoutAPI
{
	@FormUrlEncoded
	@POST
	Call<Void> logout(@Url String url, @Field("token") String token);
}