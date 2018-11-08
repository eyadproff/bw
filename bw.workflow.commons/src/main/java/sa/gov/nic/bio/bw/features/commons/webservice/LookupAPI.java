package sa.gov.nic.bio.bw.features.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/application/person-type/v1")
	Call<List<PersonType>> lookupSamisIdTypes();
	
	@GET("services-gateway-lookups/api/application/id-types/v1")
	Call<List<DocumentType>> lookupDocumentTypes();
	
	@GET("services-gateway-lookups/api/application/nationality/all/v1")
	Call<List<Country>> lookupCountries();
}