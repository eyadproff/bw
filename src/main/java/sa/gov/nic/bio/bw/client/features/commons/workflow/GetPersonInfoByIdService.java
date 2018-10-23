package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfoByIdAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.commons.TaskResponse;

public class GetPersonInfoByIdService
{
	public static TaskResponse<PersonInfo> execute(long personId, int personType)
	{
		PersonInfoByIdAPI personInfoByIdAPI = Context.getWebserviceManager().getApi(PersonInfoByIdAPI.class);
		Call<PersonInfo> apiCall = personInfoByIdAPI.getPersonInfoById(personId, personType);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}