package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.PersonInfo;

import java.util.List;

public interface PersonInfoByIdAPI
{
	@GET
	Call<PersonInfo> getPersonInfoById(@Url String url, @Query("person-id") long personId,
	                                   @Query("person-type") int personType);
	
	@GET
	Call<List<Finger>> getFingerprintsById(@Url String url, @Query("person-id") long personId);
	
	@GET
	Call<List<Integer>> getFingerprintAvailability(@Url String url, @Query("person-id") long personId);
}