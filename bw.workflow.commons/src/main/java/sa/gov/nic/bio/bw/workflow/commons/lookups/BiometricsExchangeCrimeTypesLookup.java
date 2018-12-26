package sa.gov.nic.bio.bw.workflow.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeCrimeType;
import sa.gov.nic.bio.bw.workflow.commons.webservice.LookupAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class BiometricsExchangeCrimeTypesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.biometricsExchangeCrimeTypes";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		List<BiometricsExchangeCrimeType> biometricsExchangeCrimeTypes =
										(List<BiometricsExchangeCrimeType>) Context.getUserSession().getAttribute(KEY);
		
		if(biometricsExchangeCrimeTypes == null)
		{
			LookupAPI api = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<BiometricsExchangeCrimeType>> call = api.lookupBiometricsExchangeCrimeTypes();
			TaskResponse<List<BiometricsExchangeCrimeType>> taskResponse =
																	Context.getWebserviceManager().executeApi(call);
			
			if(taskResponse.isSuccess()) biometricsExchangeCrimeTypes = taskResponse.getResult();
			else return taskResponse;
			
			Context.getUserSession().setAttribute(KEY, biometricsExchangeCrimeTypes);
		}
		
		return TaskResponse.success();
	}
}