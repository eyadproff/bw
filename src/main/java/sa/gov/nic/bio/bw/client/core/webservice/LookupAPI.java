package sa.gov.nic.bio.bw.client.core.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.Map;
import java.util.Set;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/lookups/getnichijricalenderdata/v1")
	Call<NicHijriCalendarData> lookupNicHijriCalendarData();
	
	@GET("services-gateway-lookups/api/lookups/application/menu-roles/v1")
	Call<Map<String, Set<String>>> lookupMenuRoles(@Query("app-code") String appCode);
}