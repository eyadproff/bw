package sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

import java.util.List;

public interface FingerprintInquiryAPI
{
	@FormUrlEncoded
	@POST
	Call<List<Integer>> inquireWithFingerprints(@Url String url, @Field("fingers") String collectedFingerprints,
                                                                 @Field("missing") String missingFingerprints);
}