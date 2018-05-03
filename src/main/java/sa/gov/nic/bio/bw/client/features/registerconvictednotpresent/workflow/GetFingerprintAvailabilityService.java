package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.webservice.PersonInfoByIdAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class GetFingerprintAvailabilityService
{
	public static ServiceResponse<List<Integer>> execute(long personId)
	{
		PersonInfoByIdAPI personInfoByIdAPI = Context.getWebserviceManager().getApi(PersonInfoByIdAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.getFingerprintAvailability");
		
		Call<List<Integer>> apiCall = personInfoByIdAPI.getFingerprintAvailability(url, personId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}