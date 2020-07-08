package sa.gov.nic.bio.bw.workflow.biometricsexception.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.SubmissionAndDeletionResponse;

import java.util.List;


public interface BioExclusionAPI {


    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/retrieve/v1")
    Call<List<BioExclusion>> retrieveBioExclusions(@Header("Workflow-Code") Integer workflowId,
                                                   @Header("Workflow-Tcn") Long workflowTcn,
                                                   @Field("samis-id") Integer samisId);

    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/v1")
    Call<SubmissionAndDeletionResponse> submitBioExclusions(@Header("Workflow-Code") Integer workflowId,
                                                            @Header("Workflow-Tcn") Long workflowTcn,
                                                            @Field("biometrics-exclusive") String bioExclusions);

    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/delete/v1")
    Call<SubmissionAndDeletionResponse> deleteBioExclusions(@Header("Workflow-Code") Integer workflowId,
                                                            @Header("Workflow-Tcn") Long workflowTcn,
                                                            @Field("seq-numbers") String seqNum,
                                                            @Field("deleter-id") Long deleterId);

}
