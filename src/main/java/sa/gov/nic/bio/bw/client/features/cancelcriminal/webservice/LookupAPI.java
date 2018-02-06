package sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

import java.util.List;

public interface LookupAPI
{
	@GET
	Call<List<PersonIdType>> lookupPersonIdTypes(@Url String url);
}