package sa.gov.nic.bio.bw.client.core.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

import java.util.Map;
import java.util.Set;

public interface LookupAPI
{
	@GET
	Call<NicHijriCalendarData> lookupNicHijriCalendarData(@Url String url);
	
	@GET
	Call<Map<String, Set<String>>> lookupMenuRoles(@Url String url, @Query("app-code") String appCode);
}