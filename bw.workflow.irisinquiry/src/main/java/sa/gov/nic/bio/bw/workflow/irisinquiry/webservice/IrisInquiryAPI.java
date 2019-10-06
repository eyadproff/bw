package sa.gov.nic.bio.bw.workflow.irisinquiry.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.irisinquiry.beans.IrisInquiryStatusResult;

public interface IrisInquiryAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/iris/identification/v1")
	Call<Long> inquireWithIris(@Header("Workflow-Code") Integer workflowId,
	                           @Header("Workflow-Tcn") Long workflowTcn,
	                           @Field("right-iris") String rightIrisBase64,
	                           @Field("left-iris") String leftIrisBase64);
	
	@GET("services-gateway-biooperation/api/iris/identification/status/v1")
	Call<IrisInquiryStatusResult> checkIrisInquiryStatus(@Header("Workflow-Code") Integer workflowId,
	                                                     @Header("Workflow-Tcn") Long workflowTcn,
	                                                     @Query("tcn") Long tcn);
}