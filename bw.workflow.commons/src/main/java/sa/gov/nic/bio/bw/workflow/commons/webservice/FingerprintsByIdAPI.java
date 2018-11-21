package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import java.util.List;

public interface FingerprintsByIdAPI
{
	@GET("services-gateway-biooperation/api/fingerprint/images/v1")
	Call<List<Finger>> getFingerprintsById(@Header("Workflow-Code") Integer workflowId,
	                                       @Header("Workflow-Tcn") Long workflowTcn,
	                                       @Query("person-id") long personId);
	
	@GET("services-gateway-biooperation/api/fingerprint/available/v1")
	Call<List<Integer>> getFingerprintAvailability(@Header("Workflow-Code") Integer workflowId,
	                                               @Header("Workflow-Tcn") Long workflowTcn,
	                                               @Query("person-id") long personId);
}