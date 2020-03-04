package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.beans.MiscreantFingerprintsRegistrationResponse;

public interface MiscreantFingerprintsAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/miscreant/fingers/registration/v1")
	Call<MiscreantFingerprintsRegistrationResponse> registerCriminalFingerprints(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn,
	                                                                             @Field("miscreant-id") Long miscreantId, @Field("fingers") String fingerList,
	                                                                             @Field("missing") String missingFingers);
	
	@GET("services-gateway-biooperation/api/miscreant/fingers/registration/status/v1")
	Call<Void> checkMiscreantFingerprintsStatus(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn, @Query("tcn") Long tcn);
}