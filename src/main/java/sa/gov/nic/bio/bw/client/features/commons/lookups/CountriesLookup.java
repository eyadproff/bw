package sa.gov.nic.bio.bw.client.features.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class CountriesLookup implements Callable<TaskResponse<Void>>, AppLogger
{
	public static final String KEY = "lookups.countries";
	
	@Override
	public TaskResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<CountryBean> countries = (List<CountryBean>) Context.getUserSession().getAttribute(KEY);
		
		if(countries == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CountryBean>> nationalitiesCall = lookupAPI.lookupCountries();
			TaskResponse<List<CountryBean>> nationalitiesResponse = Context.getWebserviceManager()
																			  .executeApi(nationalitiesCall);
			
			if(nationalitiesResponse.isSuccess())
			{
				countries = nationalitiesResponse.getResult();
				countries.removeIf(nationalityBean -> nationalityBean.getMofaNationalityCode().trim().isEmpty());
			}
			else return TaskResponse.failure(nationalitiesResponse.getErrorCode(),
			                                 nationalitiesResponse.getException(),
			                                 nationalitiesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, countries);
			LOGGER.info(KEY + " = " + countries);
		}
		
		return TaskResponse.success(null);
	}
}