package sa.gov.nic.bio.bw.client.features.faceverification.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.logging.Logger;

public class LookupService
{
	private static final Logger LOGGER = Logger.getLogger(LookupService.class.getName());
	
	public static ServiceResponse<Void> execute()
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<CountryBean> countries = (List<CountryBean>) userSession.getAttribute("lookups.countries");
		
		if(countries == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CountryBean>> nationalitiesCall = lookupAPI.lookupNationalities();
			ServiceResponse<List<CountryBean>> nationalitiesResponse = Context.getWebserviceManager()
																				  .executeApi(nationalitiesCall);
			
			if(nationalitiesResponse.isSuccess())
			{
				countries = nationalitiesResponse.getResult();
				countries.removeIf(countryBean -> countryBean.getMofaNationalityCode().trim().isEmpty());
			}
			else return ServiceResponse.failure(nationalitiesResponse.getErrorCode(),
			                                    nationalitiesResponse.getException(),
			                                    nationalitiesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.countries", countries);
		}
		
		LOGGER.info("countries = " + countries);
		
		return ServiceResponse.success(null);
	}
}