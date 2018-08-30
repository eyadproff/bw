package sa.gov.nic.bio.bw.client.features.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/application/nationality/all/v1")
	Call<List<CountryBean>> lookupNationalities();
	
	@GET("services-gateway-lookups/api/application/person-type/v1")
	Call<List<PersonIdType>> lookupPersonIdTypes();
	
	@GET("services-gateway-lookups/api/application/crime-type/v1")
	Call<List<CrimeType>> lookupCrimeTypes();
	
	@GET("services-gateway-lookups/api/application/id-types/v1")
	Call<List<IdType>> lookupIdTypes();
}