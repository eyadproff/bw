package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.commons.beans.FingerprintInquiryStatusResult;

public interface FingerprintInquiryAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/fingerprint/inquiry/v1")
	Call<Integer> inquireWithFingerprints(@Header("Workflow-Code") Integer workflowId,
	                                      @Header("Workflow-Tcn") Long workflowTcn,
	                                      @Field("fingers") String collectedFingerprints,
	                                      @Field("missing") String missingFingerprints);
	
	@GET("services-gateway-biooperation/api/fingerprint/inquiry/status/v2")
	Call<FingerprintInquiryStatusResult> checkFingerprintsInquiryStatus(@Header("Workflow-Code") Integer workflowId,
	                                                                    @Header("Workflow-Tcn") Long workflowTcn,
	                                                                    @Query("inquiry-id") int inquiryId);
	
	@GET("services-gateway-biooperation/api/fingerprint/inquiry/status/v3")
	Call<FingerprintInquiryStatusResult> checkFingerprintsInquiryStatusWithoutCriminal(@Header("Workflow-Code") Integer workflowId,
	                                                                                   @Header("Workflow-Tcn") Long workflowTcn,
	                                                                                   @Query("inquiry-id") int inquiryId);
}