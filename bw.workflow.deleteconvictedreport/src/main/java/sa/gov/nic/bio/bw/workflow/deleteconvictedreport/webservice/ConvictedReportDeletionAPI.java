package sa.gov.nic.bio.bw.workflow.deleteconvictedreport.webservice;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ConvictedReportDeletionAPI
{
	@DELETE("services-gateway-biooperation/api/criminal/report/deletion/v1")
	Call<Void> deleteConvictedReportByReportNumber(@Header("Workflow-Code") Integer workflowId,
                                                   @Header("Workflow-Tcn") Long workflowTcn,
	                                               @Query("report-number") Long reportNumber);
}