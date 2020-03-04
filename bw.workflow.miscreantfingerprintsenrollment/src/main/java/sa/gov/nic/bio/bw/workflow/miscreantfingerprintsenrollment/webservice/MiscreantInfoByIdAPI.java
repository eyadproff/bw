package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.beans.MiscreantInfo;

public interface MiscreantInfoByIdAPI
{
	@GET("services-gateway-demographic/api/miscreant/info/v1")
	Call<MiscreantInfo> getMiscreantInfoById(@Header("Workflow-Code") Integer workflowId, @Header("Workflow-Tcn") Long workflowTcn, @Query("miscreant-id") long miscreantId);
}