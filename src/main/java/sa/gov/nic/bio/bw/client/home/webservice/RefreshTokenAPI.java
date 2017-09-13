package sa.gov.nic.bio.bw.client.home.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RefreshTokenAPI
{
	@FormUrlEncoded
	@POST("/services-gateway-identity/api/identity/token/refresh/v1")
	Call<RefreshTokenBean> refreshToken(@Field("token") String token);
}