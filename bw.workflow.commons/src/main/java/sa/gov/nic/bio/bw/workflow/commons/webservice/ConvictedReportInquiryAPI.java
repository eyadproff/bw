package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.DisCriminalReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.SearchQueryResult;

import java.util.List;

public interface ConvictedReportInquiryAPI
{
	@GET("services-gateway-biooperation/api/xafis/report/v1")
	Call<List<ConvictedReport>> inquireConvictedReportByGeneralFileNumber(
																	  @Header("Workflow-Code") Integer workflowId,
	                                                                  @Header("Workflow-Tcn") Long workflowTcn,
                                                                      @Query("general-number") long generalFileNumber);
	
	@GET("services-gateway-biooperation/api/criminal/info/dis/{general-file-number}/v1")
	Call<List<DisCriminalReport>> inquireConvictedReportFromDisByGeneralFileNumber(
																@Header("Workflow-Code") Integer workflowId,
																@Header("Workflow-Tcn") Long workflowTcn,
																@Path("general-file-number") long generalFileNumber);
	
	@GET("services-gateway-demographic/api/criminal/info/basic/custom/v1")
	Call<SearchQueryResult<ConvictedReport>> inquireConvictedReportBySearchCriteria(
																@Header("Workflow-Code") Integer workflowId,
																@Header("Workflow-Tcn") Long workflowTcn,
																@Query("general-file-number") Long generalFileNumber,
																@Query("report-number") Long reportNumber,
																@Query("judgement-number") String judgementNumber,
																@Query("samis-id") Long samisId,
																@Query("prisoner-number") Long prisonerNumber,
																@Query("document-id") String documentId,
																@Query("judgement-date-from") Long judgementDateFrom,
																@Query("judgement-date-to") Long judgementDateTo,
																@Query("first-name") String firstName,
																@Query("father-name") String fatherName,
																@Query("grand-father-name") String gFatherName,
																@Query("family-name") String familyName,
																@Query("page-start") Integer pageStart,
																@Query("page-end") Integer pageEnd);
}