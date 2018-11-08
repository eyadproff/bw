package sa.gov.nic.bio.bw.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.features.commons.webservice.PersonInfoByIdAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class GetPersonInfoByIdWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long personId;
	@Output private PersonInfo personInfo;
	
	@Override
	public void execute() throws Signal
	{
		PersonInfoByIdAPI personInfoByIdAPI = Context.getWebserviceManager().getApi(PersonInfoByIdAPI.class);
		Call<PersonInfo> apiCall = personInfoByIdAPI.getPersonInfoById(personId, 0);
		TaskResponse<PersonInfo> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		personInfo = taskResponse.getResult();
	}
}