package sa.gov.nic.bio.bw.client.core.webservice;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/lookups/getnichijricalenderdata/v1")
	Call<NicHijriCalendarData> lookupNicHijriCalendarData();
}