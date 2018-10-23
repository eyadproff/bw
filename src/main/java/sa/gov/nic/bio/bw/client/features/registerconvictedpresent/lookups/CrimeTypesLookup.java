package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.LookupAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class CrimeTypesLookup implements Callable<TaskResponse<Void>>, AppLogger
{
	public static final String KEY = "lookups.crimeTypes";
	
	@Override
	public TaskResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(KEY);
		
		if(crimeTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CrimeType>> crimeTypesCall = lookupAPI.lookupCrimeTypes();
			TaskResponse<List<CrimeType>> crimeTypesResponse = Context.getWebserviceManager()
																		 .executeApi(crimeTypesCall);
			
			if(crimeTypesResponse.isSuccess()) crimeTypes = crimeTypesResponse.getResult();
			else return TaskResponse.failure(crimeTypesResponse.getErrorCode(),
			                                 crimeTypesResponse.getException(),
			                                 crimeTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, crimeTypes);
			LOGGER.info(KEY + " = " + crimeTypes);
		}
		
		return TaskResponse.success(null);
	}
}