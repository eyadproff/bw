package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalFingerprintSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalFingerprintsRegistrationResponse;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalWorkflowSource;

public interface CriminalFingerprintsAPI
{
	@GET("services-gateway-biooperation/api/criminal/criminal-id/generation/v1")
	Call<Long> generateNewCriminalBiometricsID(@Header("Workflow-Code") Integer workflowId,
	                                           @Header("Workflow-Tcn") Long workflowTcn);
	
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/criminal/fingers/registration/v1")
	Call<CriminalFingerprintsRegistrationResponse> registerCriminalFingerprints(
																		@Header("Workflow-Code") Integer workflowId,
																		@Header("Workflow-Tcn") Long workflowTcn,
																		@Field("criminal-id") Long criminalId,
																		@Field("fingers") String fingerList,
																		@Field("palms") String palmList,
																		@Field("missing") String missingFingers,
																		@Field("workflow-source") CriminalWorkflowSource criminalWorkflowSource,
																		@Field("fingers-source") CriminalFingerprintSource criminalFingerprintSource);
	
	@GET("services-gateway-biooperation/api/criminal/fingers/registration/status/v1")
	Call<Void> checkCriminalFingerprintsStatus(@Header("Workflow-Code") Integer workflowId,
                                               @Header("Workflow-Tcn") Long workflowTcn,
	                                           @Query("tcn") Long tcn);
}