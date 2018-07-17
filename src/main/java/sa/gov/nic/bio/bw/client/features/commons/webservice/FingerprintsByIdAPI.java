package sa.gov.nic.bio.bw.client.features.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

import java.util.List;

public interface FingerprintsByIdAPI
{
	@GET
	Call<List<Finger>> getFingerprintsById(@Url String url, @Query("person-id") long personId);
	
	@GET
	Call<List<Integer>> getFingerprintAvailability(@Url String url, @Query("person-id") long personId);
}