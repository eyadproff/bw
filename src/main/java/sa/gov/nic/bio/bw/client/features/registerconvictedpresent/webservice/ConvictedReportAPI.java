package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportResponse;

public interface ConvictedReportAPI
{
	@POST("services-gateway-biooperation/api/xafis/general-file-number/v1")
	Call<Long> generateGeneralFileNumber();
	
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/report/v1")
	Call<ConvictedReportResponse> submitConvictedReport(@Field("convicted-report") String convictedReport);
}