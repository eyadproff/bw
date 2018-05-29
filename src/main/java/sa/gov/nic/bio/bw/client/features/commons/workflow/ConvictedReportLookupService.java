package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.IdType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonIdType;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.logging.Logger;

public class ConvictedReportLookupService
{
	private static final Logger LOGGER = Logger.getLogger(ConvictedReportLookupService.class.getName());
	
	public static ServiceResponse<Void> execute()
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
														userSession.getAttribute("lookups.countries");
		@SuppressWarnings("unchecked") List<PersonIdType> personIdTypes = (List<PersonIdType>)
														userSession.getAttribute("lookups.personIdTypes");
		@SuppressWarnings("unchecked") List<CrimeType> crimeTypes = (List<CrimeType>)
														userSession.getAttribute("lookups.crimeTypes");
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
				countries.removeIf(nationalityBean -> nationalityBean.getMofaNationalityCode().trim().isEmpty());
			}
			else return ServiceResponse.failure(nationalitiesResponse.getErrorCode(),
			                                    nationalitiesResponse.getException(),
			                                    nationalitiesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.countries", countries);
			LOGGER.info("countries = " + countries);
		}
		
		if(personIdTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupPersonIdTypes");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PersonIdType>> personIdTypesCall = lookupAPI.lookupPersonIdTypes(url);
			ServiceResponse<List<PersonIdType>> personIdTypesResponse = Context.getWebserviceManager()
																		       .executeApi(personIdTypesCall);
			
			if(personIdTypesResponse.isSuccess()) personIdTypes = personIdTypesResponse.getResult();
			else return ServiceResponse.failure(personIdTypesResponse.getErrorCode(),
			                                    personIdTypesResponse.getException(),
			                                    personIdTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.personIdTypes", personIdTypes);
			LOGGER.info("personIdTypes = " + personIdTypes);
		}
		
		if(crimeTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupCrimeTypes");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CrimeType>> crimeTypesCall = lookupAPI.lookupCrimeTypes(url);
			ServiceResponse<List<CrimeType>> crimeTypesResponse = Context.getWebserviceManager()
																		 .executeApi(crimeTypesCall);
			
			if(crimeTypesResponse.isSuccess()) crimeTypes = crimeTypesResponse.getResult();
			else return ServiceResponse.failure(crimeTypesResponse.getErrorCode(),
			                                    crimeTypesResponse.getException(),
			                                    crimeTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.crimeTypes", crimeTypes);
			LOGGER.info("crimeTypes = " + crimeTypes);
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
		
		return ServiceResponse.success(null);
	}
}