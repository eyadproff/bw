package sa.gov.nic.bio.bw.workflow.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class PersonTypesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.personTypes";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>) Context.getUserSession().getAttribute(KEY);
		
		if(personTypes == null)
		{
			LookupAPI api = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PersonType>> call = api.lookupSamisIdTypes();
			TaskResponse<List<PersonType>> taskResponse = Context.getWebserviceManager().executeApi(call);
			
			if(taskResponse.isSuccess()) personTypes = taskResponse.getResult();
			else return taskResponse;
			
			Context.getUserSession().setAttribute(KEY, personTypes);
		}
		
		return TaskResponse.success();
	}
}