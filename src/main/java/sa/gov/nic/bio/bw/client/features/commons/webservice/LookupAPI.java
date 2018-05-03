package sa.gov.nic.bio.bw.client.features.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

import java.util.List;

public interface LookupAPI
{
	@GET
	Call<List<NationalityBean>> lookupNationalities(@Url String url);
	
	@GET
	Call<List<PersonIdType>> lookupPersonIdTypes(@Url String url);
	
	@GET
	Call<List<CrimeType>> lookupCrimeTypes(@Url String url);
	
	@GET
	Call<List<IdType>> lookupIdTypes(@Url String url);
}