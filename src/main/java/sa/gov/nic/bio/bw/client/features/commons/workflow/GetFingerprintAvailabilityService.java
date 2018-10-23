package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintsByIdAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class GetFingerprintAvailabilityService
{
	public static TaskResponse<List<Integer>> execute(long personId)
	{
		FingerprintsByIdAPI fingerprintsByIdAPI = Context.getWebserviceManager().getApi(FingerprintsByIdAPI.class);
		Call<List<Integer>> apiCall = fingerprintsByIdAPI.getFingerprintAvailability(personId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}