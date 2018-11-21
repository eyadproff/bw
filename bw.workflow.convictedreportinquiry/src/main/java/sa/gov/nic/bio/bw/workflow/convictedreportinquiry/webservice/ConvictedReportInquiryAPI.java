package sa.gov.nic.bio.bw.workflow.convictedreportinquiry.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReport;

import java.util.List;

public interface ConvictedReportInquiryAPI
{
	@GET("services-gateway-biooperation/api/xafis/report/v1")
	Call<List<ConvictedReport>> inquireConvictedReportByGeneralFileNumber(@Header("Workflow-Code") Integer workflowId,
	                                                                      @Header("Workflow-Tcn") Long workflowTcn,
	                                                                      @Query("general-number") long generalFileNumber);
}