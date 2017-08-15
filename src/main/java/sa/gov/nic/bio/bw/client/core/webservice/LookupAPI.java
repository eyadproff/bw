package sa.gov.nic.bio.bw.client.core.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/lookups/getnichijricalenderdata/v1")
	Call<NicHijriCalendarData> lookupNicHijriCalendarData();
}