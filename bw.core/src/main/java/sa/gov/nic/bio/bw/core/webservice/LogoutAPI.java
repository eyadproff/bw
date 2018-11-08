package sa.gov.nic.bio.bw.core.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LogoutAPI
{
	@FormUrlEncoded
	@POST("services-gateway-identity/api/logout/v1")
	Call<Void> logout(@Field("token") String token);
}