package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeadPersonRecordById
{
	@GET("services-gateway-biooperation/api/enrollment/dead-person/v1")
	Call<DeadPersonRecord> getDeadPersonRecordById(@Query("barcode") long recordId);
}