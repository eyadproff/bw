package sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.commons.beans.SearchQueryResult;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHit;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitProcessingStatus;

public interface LatentAPI
{
	@GET("services-gateway-demographic/api/latent/hits/custom/v1")
	Call<SearchQueryResult<LatentHit>> inquireLatentHitsBySearchCriteria(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn,
	                                                                     @Query("transaction-number") Long transactionNumber, @Query("civil-biometrics-id") Long civilBiometricsId,
	                                                                     @Query("person-id") Long personId, @Query("reference-number") Long referenceNumber,
	                                                                     @Query("location-id") Integer locationId, @Query("status") LatentHitProcessingStatus status,
	                                                                     @Query("entry-date-from") Long entryDateFrom, @Query("entry-date-to") Long entryDateTo,
	                                                                     @Query("page-start") Integer pageStart, @Query("page-end") Integer pageEnd);
	
	@GET("services-gateway-demographic/api/latent/hit/details/v1")
	Call<LatentHitDetails> getLatentHitDetails(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn,
	                                           @Query("transaction-number") Long transactionNumber);
}