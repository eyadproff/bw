package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.IdType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.logging.Logger;

public class PersonInfoLookupService
{
	private static final Logger LOGGER = Logger.getLogger(PersonInfoLookupService.class.getName());
	
	public static ServiceResponse<Void> execute()
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<CountryBean> countries = (List<CountryBean>) userSession.getAttribute("lookups.countries");
		
		@SuppressWarnings("unchecked") List<IdType> idTypes = (List<IdType>)
				userSession.getAttribute("lookups.idTypes");
		
		if(countries == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupNationalities");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CountryBean>> nationalitiesCall = lookupAPI.lookupNationalities(url);
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
		
		if(idTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupIdTypes");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<IdType>> idTypesCall = lookupAPI.lookupIdTypes(url);
			ServiceResponse<List<IdType>> idTypesResponse = Context.getWebserviceManager().executeApi(idTypesCall);
			
			if(idTypesResponse.isSuccess()) idTypes = idTypesResponse.getResult();
			else return ServiceResponse.failure(idTypesResponse.getErrorCode(),
			                                    idTypesResponse.getException(),
			                                    idTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.idTypes", idTypes);
			LOGGER.info("idTypes = " + idTypes);
		}
		
		LOGGER.info("countries = " + countries);
		
		return ServiceResponse.success(null);
	}
}