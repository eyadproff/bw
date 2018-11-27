package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.BiometricsExchangeCrimeType;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.BiometricsExchangeParty;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CrimeType;

import java.util.List;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/application/crime-type/v1")
	Call<List<CrimeType>> lookupCrimeTypes();
	
	@GET("services-gateway-lookups/api/bio-exchange/parties/v1")
	Call<List<BiometricsExchangeParty>> lookupBiometricsExchangeParties();
	
	@GET("services-gateway-lookups/api/bio-exchange/crimes/v1")
	Call<List<BiometricsExchangeCrimeType>> lookupBiometricsExchangeCrimeTypes();
}