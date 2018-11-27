package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.beans.DeadPersonRecord;

public interface DeadPersonRecordById
{
	@GET("services-gateway-biooperation/api/enrollment/dead-person/v1")
	Call<DeadPersonRecord> getDeadPersonRecordById(@Header("Workflow-Code") Integer workflowId,
	                                               @Header("Workflow-Tcn") Long workflowTcn,
	                                               @Query("barcode") long recordId);
}