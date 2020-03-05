package sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice;

import retrofit2.Call;

import retrofit2.http.*;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.BioExclusion;

import java.util.List;

public interface CitizenEnrollmentAPI {


    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/retrieve/v1")
    Call<List<BioExclusion>> retrieveBioExclusions(@Header("Workflow-Code") Integer workflowId,
                                                   @Header("Workflow-Tcn") Long workflowTcn,
                                                   @Field("samis-id") Integer samisId);

    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/iris/registration/v1")
    Call<Long> enrollPerson(@Header("Workflow-Code") Integer workflowId,
                                         @Header("Workflow-Tcn") Long workflowTcn,
                                         @Field("person-id") Long personId,
                                         @Field("person-type")  Integer personType,
                                         @Field("fingers") String fingers,
                                         @Field("missing") String missing,
                                         @Field("face-image") String faceImage,
                                         @Field("birth-date") String birthDate,
                                         @Field("gender") Integer gender,
                                         @Field("supervisor-id") Long supervisorId);

    @GET("services-gateway-biooperation/api/iris/registration/status/v1")
    Call<Void> checkCitizenRegistration(@Header("Workflow-Code") Integer workflowId,
                                        @Header("Workflow-Tcn") Long workflowTcn,
                                        @Query("tcn") Long tcn);
}
