package sa.gov.nic.bio.bw.features.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import sa.gov.nic.bio.bw.features.commons.webservice.CrimeType;

import java.util.List;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/application/crime-type/v1")
	Call<List<CrimeType>> lookupCrimeTypes();
}