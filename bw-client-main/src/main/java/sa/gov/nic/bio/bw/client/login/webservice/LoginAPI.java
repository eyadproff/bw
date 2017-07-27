package sa.gov.nic.bio.bw.client.login.webservice;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginAPI
{
	@FormUrlEncoded
	@POST("services-gateway-identity/api/identity/login/v1")
	Call<LoginBean> login(@Field("username") String username, @Field("password") String password, @Field("ipaddress") String ipAddress);
}