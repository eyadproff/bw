package sa.gov.nic.bio.bw.workflow.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeParty;
import sa.gov.nic.bio.bw.workflow.commons.webservice.LookupAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class BiometricsExchangePartiesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.biometricsExchangeParties";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		List<BiometricsExchangeParty> biometricsExchangeParties =
											(List<BiometricsExchangeParty>) Context.getUserSession().getAttribute(KEY);
		
		if(biometricsExchangeParties == null)
		{
			LookupAPI api = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<BiometricsExchangeParty>> call = api.lookupBiometricsExchangeParties();
			TaskResponse<List<BiometricsExchangeParty>> taskResponse = Context.getWebserviceManager().executeApi(call);
			
			if(taskResponse.isSuccess()) biometricsExchangeParties = taskResponse.getResult();
			else return taskResponse;
			
			Context.getUserSession().setAttribute(KEY, biometricsExchangeParties);
		}
		
		return TaskResponse.success();
	}
}