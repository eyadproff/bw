package sa.gov.nic.bio.bw.workflow.criminaltransactions.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.beans.CriminalTransactionType;

import java.util.List;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/criminal/transaction/types/v1")
	Call<List<CriminalTransactionType>> lookupCriminalTransactionTypes();
}