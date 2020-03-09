package sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.*;

public interface IrisRegistrationAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/iris/registration/v1")
	Call<Long> submitIrisRegistration(@Header("Workflow-Code") Integer workflowId,
                                      @Header("Workflow-Tcn") Long workflowTcn,
                                      @Field("person-id") Long personId,
                                      @Field("right-iris") String rightIrisBase64,
                                      @Field("left-iris") String leftIrisBase64);

	@GET("services-gateway-biooperation/api/iris/registration/status/v1")
	Call<Void> checkIrisRegistration(@Header("Workflow-Code") Integer workflowId,
                                     @Header("Workflow-Tcn") Long workflowTcn,
                                     @Query("tcn") Long tcn);
}