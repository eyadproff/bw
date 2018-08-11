package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintsByIdAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class GetFingerprintAvailabilityService
{
	public static ServiceResponse<List<Integer>> execute(long personId)
	{
		FingerprintsByIdAPI fingerprintsByIdAPI = Context.getWebserviceManager().getApi(FingerprintsByIdAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.getFingerprintAvailability");
		
		Call<List<Integer>> apiCall = fingerprintsByIdAPI.getFingerprintAvailability(url, personId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}