package sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.commons.beans.SearchQueryResult;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentInfo;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJob;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJobDetails;

public interface LatentAPI
{
	@GET("services-gateway-biooperation/api/latent/info/v1")
	Call<LatentInfo> getLatentInfo(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn, @Query("latent-id") Long latentId);
	
	@GET("services-gateway-biooperation/api/latent/jobs/info/custom/v1")
	Call<SearchQueryResult<LatentJob>> inquireLatentJobsBySearchCriteria(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn,
	                                                                     @Query("job-id") Long jobId, @Query("civil-biometrics-id") Long civilBiometricsId,
	                                                                     @Query("samis-id") Long personId, @Query("tcn") Long tcn,
	                                                                     @Query("location-id") Integer locationId, @Query("status") String status,
	                                                                     @Query("create-date-from") Long createDateFrom, @Query("create-date-to") Long createDateTo,
	                                                                     @Query("page-start") Integer pageStart, @Query("page-end") Integer pageEnd);
	
	@GET("services-gateway-biooperation/api/latent/jobs/info/tcn/v1")
	Call<LatentJobDetails> getLatentJobDetails(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn, @Query("tcn") Long tcn);
	
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/latent/job/decision/submit/v1")
	Call<Void> addDecisionToLatentHit(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn, @Field("decision") String decisionHistoryJson);
}