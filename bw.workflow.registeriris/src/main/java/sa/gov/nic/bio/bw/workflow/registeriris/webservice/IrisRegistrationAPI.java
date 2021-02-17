package sa.gov.nic.bio.bw.workflow.registeriris.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface IrisRegistrationAPI
{

	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/iris/addition/v1")
	Call<Void> submitIrisRegistration(@Header("Workflow-Code") Integer workflowId,
			@Header("Workflow-Tcn") Long workflowTcn,
			@Field("person-id") Long personId,
			@Field("right-iris") String rightIrisBase64,
			@Field("left-iris") String leftIrisBase64,
			@Field("supervisor-id") Long supervisorId);

	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/iris/replacement/v1")
	Call<Void> replaceIris(@Header("Workflow-Code") Integer workflowId,
	                                  @Header("Workflow-Tcn") Long workflowTcn,
	                                  @Field("person-id") Long personId,
	                                  @Field("right-iris") String rightIrisBase64,
	                                  @Field("left-iris") String leftIrisBase64,
									  @Field("supervisor-id") Long supervisorId);
}