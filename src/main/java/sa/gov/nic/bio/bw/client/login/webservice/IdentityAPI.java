package sa.gov.nic.bio.bw.client.login.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import sa.gov.nic.bio.bw.client.core.webservice.RefreshTokenBean;

public interface IdentityAPI
{
	@FormUrlEncoded
	@POST
	Call<LoginBean> login(@Url String url, @Field("username") String username, @Field("password") String password, @Field("ipaddress") String ipAddress, @Field("appcode") String appCode, @Field("useraccounttype") String userAccountType);
	
	@FormUrlEncoded
	@POST
	Call<Void> logout(@Url String url, @Field("token") String token);
	
	@FormUrlEncoded
	@POST
	Call<Boolean> changePassword(@Url String url, @Field("username") String username, @Field("old-password") String oldPassword, @Field("new-password") String newPassword, @Field("app-code") String appCode, @Field("user-account-type") String userAccountType, @Field("ip-address") String ipAddress);
	
	@FormUrlEncoded
	@POST
	Call<RefreshTokenBean> refreshToken(@Url String url, @Field("token") String token);
}