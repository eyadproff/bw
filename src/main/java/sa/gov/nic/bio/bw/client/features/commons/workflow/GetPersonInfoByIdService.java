package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfoByIdAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class GetPersonInfoByIdService
{
	public static ServiceResponse<PersonInfo> execute(long personId, int personType)
	{
		PersonInfoByIdAPI personInfoByIdAPI = Context.getWebserviceManager().getApi(PersonInfoByIdAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.getPersonInfoById");
		
		Call<PersonInfo> apiCall = personInfoByIdAPI.getPersonInfoById(url, personId, personType);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}