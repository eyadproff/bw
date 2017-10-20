package sa.gov.nic.bio.bw.client.login.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface LoginAPI
{
	@FormUrlEncoded
	@POST
	Call<LoginBean> login(@Url String url, @Field("username") String username, @Field("password") String password, @Field("ipaddress") String ipAddress, @Field("appcode") String appCode, @Field("useraccounttype") String userAccountType);
}