package sa.gov.nic.bio.bw.workflow.scfaceverification.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

public interface ScFaceVerificationAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/face/verify/wanted/v1")
	Call<PersonInfo> verifyFaceImage(@Header("Workflow-Code") Integer workflowId,
	                                 @Header("Workflow-Tcn") Long workflowTcn,
	                                 @Field("person-id") long personId, @Field("image") String imageBase64);
}