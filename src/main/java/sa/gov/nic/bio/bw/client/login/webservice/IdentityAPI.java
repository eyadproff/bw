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
	Call<LoginBean> login(@Url String url, @Field("username") String username, @Field("password") String password,
	                      @Field("app-code") String appCode, @Field("user-account-type") String userAccountType);
	
	@FormUrlEncoded
	@POST
	Call<LoginBean> loginByFingerprint(@Url String url, @Field("user-name") String username,
	                                   @Field("finger-position") int fingerPosition,
	                                   @Field("finger-image") String fingerImage, @Field("app-code") String appCode);
	
	@FormUrlEncoded
	@POST
	Call<Void> logout(@Url String url, @Field("token") String token);
	
	@FormUrlEncoded
	@POST
	Call<Boolean> changePassword(@Url String url, @Field("username") String username,
	                             @Field("old-password") String oldPassword, @Field("new-password") String newPassword,
	                             @Field("app-code") String appCode, @Field("user-account-type") String userAccountType);
	
	@FormUrlEncoded
	@POST
	Call<RefreshTokenBean> refreshToken(@Url String url, @Field("token") String token);
}