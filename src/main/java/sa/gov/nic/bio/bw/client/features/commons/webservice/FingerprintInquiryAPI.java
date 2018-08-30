package sa.gov.nic.bio.bw.client.features.commons.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FingerprintInquiryAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/fingerprint/inquiry/v1")
	Call<Integer> inquireWithFingerprints(@Field("fingers") String collectedFingerprints,
                                          @Field("missing") String missingFingerprints);
	
	@GET("services-gateway-biooperation/api/fingerprint/inquiry/status/v1")
	Call<FingerprintInquiryStatusResult> checkFingerprintsInquiryStatus(@Query("inquiry-id") int inquiryId);
}