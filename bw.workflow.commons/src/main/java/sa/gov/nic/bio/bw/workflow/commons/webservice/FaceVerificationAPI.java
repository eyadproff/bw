package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FaceVerificationAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/face/verify/basic/v1")
	Call<Boolean> verifyFace(@Header("Workflow-Code") Integer workflowId,
	                        @Header("Workflow-Tcn") Long workflowTcn,
	                        @Field("person-id") Long personId,
	                        @Field("image") String image);
}