package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface FingerprintInquiryAPI
{
	@FormUrlEncoded
	@POST
	Call<Integer> inquireWithFingerprints(@Url String url, @Field("fingers") String collectedFingerprints,
                                                           @Field("missing") String missingFingerprints);
	
	@GET
	Call<FingerprintInquiryStatusResult> checkFingerprintsInquiryStatus(@Url String url,
	                                                                    @Query("inquiry-id") int inquiryId);
}