package sa.gov.nic.bio.bw.workflow.biometricsexception.webservice;

import retrofit2.Call;
import retrofit2.http.*;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.SubmissionAndDeletionResponse;

import java.util.List;


public interface BioExclusionAPI {


    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/retrieve/v1")
    Call<List<BioExclusion>> retrieveBioExclusions(@Field("samis-id") Integer samisId);

    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/v1")
    Call<SubmissionAndDeletionResponse> submitBioExclusions(@Field("biometrics-exclusive") String bioExclusions);

    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/delete/v1")
    Call<SubmissionAndDeletionResponse> deleteBioExclusions(@Field("seq-numbers") String seqNum,
                                                            @Field("deleter-id") Long deleterId);
}
