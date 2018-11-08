package sa.gov.nic.bio.bw.features.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.features.commons.webservice.PersonType;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class SamisIdTypesLookup implements Callable<TaskResponse<Void>>, AppLogger
{
	public static final String KEY = "lookups.samisIdTypes";
	
	@Override
	public TaskResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>) Context.getUserSession().getAttribute(KEY);
		
		if(personTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PersonType>> samisIdTypesCall = lookupAPI.lookupSamisIdTypes();
			TaskResponse<List<PersonType>> samisIdTypesResponse = Context.getWebserviceManager()
																		     .executeApi(samisIdTypesCall);
			
			if(samisIdTypesResponse.isSuccess()) personTypes = samisIdTypesResponse.getResult();
			else return TaskResponse.failure(samisIdTypesResponse.getErrorCode(),
			                                 samisIdTypesResponse.getException(),
			                                 samisIdTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, personTypes);
			LOGGER.info(KEY + " = " + personTypes);
		}
		
		return TaskResponse.success(null);
	}
}