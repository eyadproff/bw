package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.webservice;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.beans.CriminalFingerprintsDeletionResponse;

public interface CriminalRecordDeletionAPI
{
	@DELETE("services-gateway-biooperation/api/criminal/reports/deletion/v1")
	Call<Void> deleteConvictedReportsByCriminalBiometricsId(@Header("Workflow-Code") Integer workflowId,
	                                                        @Header("Workflow-Tcn") Long workflowTcn,
	                                                        @Query("criminal-id") Long criminalBiometricsId);
	
	@DELETE("services-gateway-biooperation/api/criminal/fingers/deletion/v1")
	Call<CriminalFingerprintsDeletionResponse> deleteCriminalFingerprints(@Header("Workflow-Code") Integer workflowId,
	                                                                      @Header("Workflow-Tcn") Long workflowTcn,
                                                                      @Query("criminal-id") Long criminalBiometricsId);
	
	@GET("services-gateway-biooperation/api/criminal/fingers/deletion/status/v1")
	Call<Void> checkCriminalFingerprintsDeletionStatus(@Header("Workflow-Code") Integer workflowId,
	                                                   @Header("Workflow-Tcn") Long workflowTcn,
	                                                   @Query("tcn") Long tcn);
}