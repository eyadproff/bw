package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeCrimeType;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeParty;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeType;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;

import java.util.List;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/application/person-type/v1")
	Call<List<PersonType>> lookupSamisIdTypes();
	
	@GET("services-gateway-lookups/api/application/id-types/v1")
	Call<List<DocumentType>> lookupDocumentTypes();
	
	@GET("services-gateway-lookups/api/application/nationality/all/v1")
	Call<List<Country>> lookupCountries();
	
	@GET("services-gateway-lookups/api/application/crime-type/v1")
	Call<List<CrimeType>> lookupCrimeTypes();
	
	@GET("services-gateway-lookups/api/bio-exchange/parties/v1")
	Call<List<BiometricsExchangeParty>> lookupBiometricsExchangeParties();
	
	@GET("services-gateway-lookups/api/bio-exchange/crimes/v1")
	Call<List<BiometricsExchangeCrimeType>> lookupBiometricsExchangeCrimeTypes();
}