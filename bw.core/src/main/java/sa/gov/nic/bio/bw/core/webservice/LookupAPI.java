package sa.gov.nic.bio.bw.core.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.Map;
import java.util.Set;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/calendar/hijri/v1")
	Call<NicHijriCalendarData> lookupNicHijriCalendarData();
	
	@GET("services-gateway-lookups/api/application/menu-roles/v1")
	Call<Map<String, Set<String>>> lookupMenuRoles(@Query("app-code") String appCode);
	
	@GET("services-gateway-lookups/api/application/configs/v1")
	Call<Map<String, String>> lookupAppConfigs(@Query("app-code") String appCode);
}