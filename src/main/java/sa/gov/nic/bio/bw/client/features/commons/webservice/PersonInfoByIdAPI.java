package sa.gov.nic.bio.bw.client.features.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PersonInfoByIdAPI
{
	@GET
	Call<PersonInfo> getPersonInfoById(@Url String url, @Query("person-id") long personId,
	                                   @Query("person-type") int personType);
}