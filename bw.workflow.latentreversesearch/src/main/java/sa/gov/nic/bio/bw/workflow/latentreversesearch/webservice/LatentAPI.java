package sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.commons.beans.SearchQueryResult;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.Decision;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJob;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJobStatus;

public interface LatentAPI
{
	@GET("services-gateway-biooperation/api/latent/jobs/info/custom/v1")
	Call<SearchQueryResult<LatentJob>> inquireLatentJobsBySearchCriteria(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn,
	                                                                     @Query("job-id") Long jobId, @Query("civil-biometrics-id") Long civilBiometricsId,
	                                                                     @Query("samis-id") Long personId, @Query("tcn") Long tcn,
	                                                                     @Query("location-id") Integer locationId, @Query("status") LatentJobStatus status,
	                                                                     @Query("create-date-from") Long createDateFrom, @Query("create-date-to") Long createDateTo,
	                                                                     @Query("page-start") Integer pageStart, @Query("page-end") Integer pageEnd);
	
	@GET("services-gateway-demographic/api/latent/hit/details/v1")
	Call<LatentHitDetails> getLatentHitDetails(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn,
	                                           @Query("transaction-number") Long transactionNumber);
	
	@POST("services-gateway-demographic/api/latent/hit/decision/v1")
	Call<Void> addDecisionToLatentHit(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn,
	                                  @Field("job-id") Long jobId, @Field("decision") Decision decision,
	                                  @Field("latent-number") String latentNumber, @Field("civil-biometrics-id") Long civilBiometricsId);
}