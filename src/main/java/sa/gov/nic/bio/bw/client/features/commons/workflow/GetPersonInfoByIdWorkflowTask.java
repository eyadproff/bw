package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfoByIdAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class GetPersonInfoByIdWorkflowTask implements WorkflowTask
{
	@Input(required = true) private long personId;
	@Output private PersonInfo personInfo;
	
	@Override
	public ServiceResponse<?> execute()
	{
		PersonInfoByIdAPI personInfoByIdAPI = Context.getWebserviceManager().getApi(PersonInfoByIdAPI.class);
		Call<PersonInfo> apiCall = personInfoByIdAPI.getPersonInfoById(personId, 0);
		ServiceResponse<PersonInfo> serviceResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		if(serviceResponse.isSuccess()) personInfo = serviceResponse.getResult();
		
		return serviceResponse;
	}
}