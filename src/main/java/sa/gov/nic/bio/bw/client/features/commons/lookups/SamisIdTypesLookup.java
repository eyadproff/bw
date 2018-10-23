package sa.gov.nic.bio.bw.client.features.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
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
		List<SamisIdType> samisIdTypes = (List<SamisIdType>) Context.getUserSession().getAttribute(KEY);
		
		if(samisIdTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<SamisIdType>> samisIdTypesCall = lookupAPI.lookupSamisIdTypes();
			TaskResponse<List<SamisIdType>> samisIdTypesResponse = Context.getWebserviceManager()
																		     .executeApi(samisIdTypesCall);
			
			if(samisIdTypesResponse.isSuccess()) samisIdTypes = samisIdTypesResponse.getResult();
			else return TaskResponse.failure(samisIdTypesResponse.getErrorCode(),
			                                 samisIdTypesResponse.getException(),
			                                 samisIdTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, samisIdTypes);
			LOGGER.info(KEY + " = " + samisIdTypes);
		}
		
		return TaskResponse.success(null);
	}
}