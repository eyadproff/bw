package sa.gov.nic.bio.bw.features.convictedreportinquiry.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.webservice.ConvictedReport;

import java.util.List;

public interface ConvictedReportInquiryAPI
{
	@GET("services-gateway-biooperation/api/xafis/report/v1")
	Call<List<ConvictedReport>> inquireConvictedReportByGeneralFileNumber(@Query("general-number") long generalFileNumber);
}