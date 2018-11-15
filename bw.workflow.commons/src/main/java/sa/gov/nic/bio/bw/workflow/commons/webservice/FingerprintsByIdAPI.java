package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface FingerprintsByIdAPI
{
	@GET("services-gateway-biooperation/api/fingerprint/images/v1")
	Call<List<Finger>> getFingerprintsById(@Query("person-id") long personId);
	
	@GET("services-gateway-biooperation/api/fingerprint/available/v1")
	Call<List<Integer>> getFingerprintAvailability(@Query("person-id") long personId);
}