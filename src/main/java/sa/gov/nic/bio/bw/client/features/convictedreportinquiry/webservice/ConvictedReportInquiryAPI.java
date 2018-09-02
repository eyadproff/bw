package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;

public interface ConvictedReportInquiryAPI
{
	@GET("services-gateway-biooperation/api/xafis/report/v1")
	Call<ConvictedReport> inquireConvictedReportByGeneralFileNumber(@Query("general-number") long generalFileNumber);
}