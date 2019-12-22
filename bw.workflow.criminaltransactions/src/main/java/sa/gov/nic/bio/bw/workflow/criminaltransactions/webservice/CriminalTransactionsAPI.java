package sa.gov.nic.bio.bw.workflow.criminaltransactions.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.beans.CriminalTransactionsInquiryResult;

public interface CriminalTransactionsAPI
{
	@GET("services-gateway-demographic/api/criminal/activity/info/custom/v1")
	Call<CriminalTransactionsInquiryResult> inquireCriminalTransactions(@Header("Workflow-Code") Integer workflowId,
	                                                                    @Header("Workflow-Tcn") Long workflowTcn,
	                                                                    @Query("criminal-id") Long criminalId);
}