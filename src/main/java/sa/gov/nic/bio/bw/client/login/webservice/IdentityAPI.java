package sa.gov.nic.bio.bw.client.login.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.client.core.webservice.RefreshTokenBean;

public interface IdentityAPI
{
	@FormUrlEncoded
	@POST("services-gateway-identity/api/login/v1")
	Call<LoginBean> login(@Field("username") String username, @Field("password") String password,
	                      @Field("app-code") String appCode, @Field("user-account-type") String userAccountType);
	
	@FormUrlEncoded
	@POST("services-gateway-identity/api/login/fingerprint/v2")
	Call<LoginBean> loginByFingerprint(@Field("user-name") String username,
	                                   @Field("finger-position") int fingerPosition,
	                                   @Field("finger-image") String fingerImage, @Field("app-code") String appCode);
	
	@FormUrlEncoded
	@POST("services-gateway-identity/api/logout/v1")
	Call<Void> logout(@Field("token") String token);
	
	@FormUrlEncoded
	@POST("services-gateway-identity/api/password/v1")
	Call<Boolean> changePassword(@Field("username") String username, @Field("old-password") String oldPassword,
	                             @Field("new-password") String newPassword, @Field("app-code") String appCode,
	                             @Field("user-account-type") String userAccountType);
	
	@FormUrlEncoded
	@POST("services-gateway-identity/api/token/refresh/v1")
	Call<RefreshTokenBean> refreshToken(@Field("token") String token);
}