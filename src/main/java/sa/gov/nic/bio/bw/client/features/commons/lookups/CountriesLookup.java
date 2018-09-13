package sa.gov.nic.bio.bw.client.features.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class CountriesLookup implements Callable<ServiceResponse<Void>>
{
	public static final String KEY = "lookups.countries";
	private static final Logger LOGGER = Logger.getLogger(CountriesLookup.class.getName());
	
	@Override
	public ServiceResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<CountryBean> countries = (List<CountryBean>) Context.getUserSession().getAttribute(KEY);
		
		if(countries == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CountryBean>> nationalitiesCall = lookupAPI.lookupCountries();
			ServiceResponse<List<CountryBean>> nationalitiesResponse = Context.getWebserviceManager()
																			  .executeApi(nationalitiesCall);
			
			if(nationalitiesResponse.isSuccess())
			{
				countries = nationalitiesResponse.getResult();
				countries.removeIf(nationalityBean -> nationalityBean.getMofaNationalityCode().trim().isEmpty());
			}
			else return ServiceResponse.failure(nationalitiesResponse.getErrorCode(),
			                                    nationalitiesResponse.getException(),
			                                    nationalitiesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, countries);
			LOGGER.info(KEY + " = " + countries);
		}
		
		return ServiceResponse.success(null);
	}
}