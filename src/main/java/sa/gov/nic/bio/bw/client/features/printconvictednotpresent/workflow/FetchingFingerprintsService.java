package sa.gov.nic.bio.bw.client.features.printconvictednotpresent.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.printconvictednotpresent.webservice.PersonInfoByIdAPI;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class FetchingFingerprintsService
{
	public static ServiceResponse<List<Finger>> execute(long personId)
	{
		PersonInfoByIdAPI personInfoByIdAPI = Context.getWebserviceManager().getApi(PersonInfoByIdAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.getFingerprintsById");
		
		Call<List<Finger>> apiCall = personInfoByIdAPI.getFingerprintsById(url, personId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}