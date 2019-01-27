package sa.gov.nic.bio.bw.core.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.core.beans.RefreshTokenBean;

public interface ClientErrorReportingAPI
{
	@FormUrlEncoded
	@POST("services-gateway-demographic/api/activity/client/error/v1")
	Call<RefreshTokenBean> refreshToken(@Field("client-error-log") String clientErrorLogJson);
}