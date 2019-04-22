package sa.gov.nic.bio.bw.workflow.editconvictedreport.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface EditConvictedReportAPI
{
	@FormUrlEncoded
	@POST("/services-gateway-demographic/api/criminal-system/update/full-report/v1")
	Call<Long> editFullConvictedReport(@Header("Workflow-Code") Integer workflowId,
	                                   @Header("Workflow-Tcn") Long workflowTcn,
	                                   @Field("criminal-report") String convictedReportJson);
	
	@FormUrlEncoded
	@POST("/services-gateway-demographic/api/criminal-system/update/crime-Judgment/v1")
	Call<Long> editConvictedReportWithoutPersonInfo(@Header("Workflow-Code") Integer workflowId,
	                                                @Header("Workflow-Tcn") Long workflowTcn,
	                                                @Field("criminal-report") String convictedReportJson);
}