package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.ConvictedReportResponse;

public interface ConvictedReportAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/report/v1")
	Call<ConvictedReportResponse> submitConvictedReport(@Header("Workflow-Code") Integer workflowId,
	                                                    @Header("Workflow-Tcn") Long workflowTcn,
	                                                    @Field("convicted-report") String convictedReport);
}