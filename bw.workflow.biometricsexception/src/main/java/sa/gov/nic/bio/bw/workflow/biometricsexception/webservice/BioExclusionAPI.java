package sa.gov.nic.bio.bw.workflow.biometricsexception.webservice;

import retrofit2.Call;
import retrofit2.http.*;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;

import java.util.List;


public interface BioExclusionAPI {


    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/retrieve/v1")
    Call<List<BioExclusion>> retrieveBioExclusions(@Field("samis-id") Integer samisId);

    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/v1")
    Call<Long> submitBioExclusions(@Field("bio-exclusion") String bioExclusions);

    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/delete/v1")
    Call<Long> deleteBioExclusions(@Field("seq-num") List<Integer> seqNum,
                                   @Field("deleter-id") Long deleterId);
}
