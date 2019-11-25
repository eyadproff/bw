package sa.gov.nic.bio.bw.workflow.registeriris.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

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