package sa.gov.nic.bio.bw.workflow.cancelcriminal.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CancelCriminalAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/delink/person-id/v1")
	Call<Boolean> cancelCriminalByPersonId(@Header("Workflow-Code") Integer workflowId,
	                                       @Header("Workflow-Tcn") Long workflowTcn,
	                                       @Field("person-id") long personId, @Field("person-type") int samisIdTypes,
	                                       @Field("criminal-id") long criminalId);
	
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/delink/inquiry-id/v1")
	Call<Boolean> cancelCriminalByInquiryId(@Header("Workflow-Code") Integer workflowId,
	                                        @Header("Workflow-Tcn") Long workflowTcn,
	                                        @Field("inquiry-id") long inquiryId, @Field("criminal-id") long criminalId);
}