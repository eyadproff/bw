package sa.gov.nic.bio.bw.client.features.printconvictednotpresent.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.PersonInfo;

import java.util.List;

public interface PersonInfoByIdAPI
{
	@GET
	Call<PersonInfo> getPersonInfoById(@Url String url, @Query("person-id") long personId,
	                                   @Query("person-type") int personType);
	
	@GET
	Call<List<Finger>> getFingerprintsById(@Url String url, @Query("person-id") long personId);
}