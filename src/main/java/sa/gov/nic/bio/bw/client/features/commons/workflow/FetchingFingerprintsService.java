package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintsByIdAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class FetchingFingerprintsService
{
	public static ServiceResponse<List<Finger>> execute(long personId)
	{
		FingerprintsByIdAPI fingerprintsByIdAPI = Context.getWebserviceManager().getApi(FingerprintsByIdAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.getFingerprintsById");
		
		Call<List<Finger>> apiCall = fingerprintsByIdAPI.getFingerprintsById(url, personId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}