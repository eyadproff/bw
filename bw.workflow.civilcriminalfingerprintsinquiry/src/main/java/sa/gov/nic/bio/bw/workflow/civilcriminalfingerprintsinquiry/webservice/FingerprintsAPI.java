package sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.List;

public interface FingerprintsAPI
{
	@GET("services-gateway-biooperation/api/fingerprint/images/v2")
	Call<List<Finger>> getFingerprintsByCivilBiometricsId(@Header("Workflow-Code") Integer workflowId,
	                                                      @Header("Workflow-Tcn") Long workflowTcn,
	                                                      @Query("bio-id") long civilBiometricsId);
	
	@GET("services-gateway-biooperation/api/fingerprint/available/v2")
	Call<List<Integer>> getFingerprintsAvailabilityByCivilBiometricsId(@Header("Workflow-Code") Integer workflowId,
	                                                                   @Header("Workflow-Tcn") Long workflowTcn,
	                                                                   @Query("bio-id") long civilBiometricsId);
	
	@GET("services-gateway-biooperation/api/fingerprint/images/v2") // TODO
	Call<List<Finger>> getFingerprintsByCriminalBiometricsId(@Header("Workflow-Code") Integer workflowId,
	                                                         @Header("Workflow-Tcn") Long workflowTcn,
	                                                         @Query("bio-id") long criminalBiometricsId);
	
	@GET("services-gateway-biooperation/api/fingerprint/available/v2") // TODO
	Call<List<Integer>> getFingerprintsAvailabilityByCriminalBiometricsId(@Header("Workflow-Code") Integer workflowId,
	                                                                      @Header("Workflow-Tcn") Long workflowTcn,
	                                                                      @Query("bio-id") long criminalBiometricsId);
}