package sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice;

import retrofit2.Call;

import retrofit2.http.*;


public interface CitizenEnrollmentAPI {


    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/register/v1")
    Call<Boolean> enrollPerson(@Header("Workflow-Code") Integer workflowId,
                            @Header("Workflow-Tcn") Long workflowTcn,
                            @Field("person-id") Long personId,
                            @Field("person-type") Integer personType,
                            @Field("fingers") String fingers,
                            @Field("missing") String missing,
                            @Field("face-image") String faceImage,
                            @Field("birth-date") String birthDate,
                            @Field("gender") Integer gender,
                            @Field("supervisor-id") Long supervisorId);

    @GET("services-gateway-biooperation/api/enrollment/status/v1")
    Call<Integer> checkCitizenRegistration(@Header("Workflow-Code") Integer workflowId,
                                        @Header("Workflow-Tcn") Long workflowTcn,
                                        @Query("person-id") Long personId);


}
