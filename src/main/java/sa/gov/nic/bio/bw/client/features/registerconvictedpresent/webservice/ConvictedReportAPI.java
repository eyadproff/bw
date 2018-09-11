package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportResponse;

public interface ConvictedReportAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/general-file-number/v2")
	Call<Long> generateGeneralFileNumber(@Field("personId") Long personId, @Field("bioId") Long bioId);
	
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/report/v1")
	Call<ConvictedReportResponse> submitConvictedReport(@Field("convicted-report") String convictedReport);
}