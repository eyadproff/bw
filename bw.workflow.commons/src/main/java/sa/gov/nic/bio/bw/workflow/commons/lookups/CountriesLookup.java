package sa.gov.nic.bio.bw.workflow.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.webservice.LookupAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class CountriesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.countries";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(KEY);
		
		if(countries == null)
		{
			LookupAPI api = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<Country>> call = api.lookupCountries();
			TaskResponse<List<Country>> taskResponse = Context.getWebserviceManager().executeApi(call);
			
			if(taskResponse.isSuccess())
			{
				countries = taskResponse.getResult();
				countries.removeIf(nationalityBean -> nationalityBean.getMofaNationalityCode().trim().isEmpty());
			}
			else return taskResponse;
			
			Context.getUserSession().setAttribute(KEY, countries);
		}
		
		return TaskResponse.success();
	}
}