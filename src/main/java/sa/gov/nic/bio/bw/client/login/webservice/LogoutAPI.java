package sa.gov.nic.bio.bw.client.login.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

public interface LogoutAPI
{
	@FormUrlEncoded
	@POST("services-gateway-identity/api/identity/logout/v1")
	Call<Void> logout(@Field("token") String token);
}