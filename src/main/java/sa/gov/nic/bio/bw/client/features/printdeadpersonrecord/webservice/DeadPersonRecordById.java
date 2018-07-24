package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface DeadPersonRecordById
{
	@GET
	Call<DeadPersonRecord> getDeadPersonRecordById(@Url String url, @Query("barcode") long recordId);
}