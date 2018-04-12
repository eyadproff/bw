package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
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
		
		@SuppressWarnings("unchecked") List<NationalityBean> nationalities = (List<NationalityBean>)
														userSession.getAttribute("lookups.nationalities");
		@SuppressWarnings("unchecked") List<PersonIdType> personIdTypes = (List<PersonIdType>)
														userSession.getAttribute("lookups.personIdTypes");
		@SuppressWarnings("unchecked") List<CrimeType> crimeTypes = (List<CrimeType>)
														userSession.getAttribute("lookups.crimeTypes");
		
		if(nationalities == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupNationalities");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<NationalityBean>> nationalitiesCall = lookupAPI.lookupNationalities(url);
			ServiceResponse<List<NationalityBean>> nationalitiesResponse = Context.getWebserviceManager()
																				  .executeApi(nationalitiesCall);
			
			if(nationalitiesResponse.isSuccess())
			{
				nationalities = nationalitiesResponse.getResult();
				nationalities.removeIf(nationalityBean -> nationalityBean.getMofaNationalityCode().trim().isEmpty());
			}
			else return ServiceResponse.failure(nationalitiesResponse.getErrorCode(),
			                                    nationalitiesResponse.getException(),
			                                    nationalitiesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.nationalities", nationalities);
		}
		
		if(personIdTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupPersonIdTypes");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PersonIdType>> visaTypesCall = lookupAPI.lookupPersonIdTypes(url);
			ServiceResponse<List<PersonIdType>> personIdTypesResponse = Context.getWebserviceManager()
																		   .executeApi(visaTypesCall);
			
			if(personIdTypesResponse.isSuccess()) personIdTypes = personIdTypesResponse.getResult();
			else return ServiceResponse.failure(personIdTypesResponse.getErrorCode(),
			                                    personIdTypesResponse.getException(),
			                                    personIdTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.personIdTypes", personIdTypes);
		}
		
		if(crimeTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupCrimeTypes");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CrimeType>> visaTypesCall = lookupAPI.lookupCrimeTypes(url);
			ServiceResponse<List<CrimeType>> crimeTypesResponse = Context.getWebserviceManager()
																			.executeApi(visaTypesCall);
			
			if(crimeTypesResponse.isSuccess()) crimeTypes = crimeTypesResponse.getResult();
			else return ServiceResponse.failure(crimeTypesResponse.getErrorCode(),
			                                    crimeTypesResponse.getException(),
			                                    crimeTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.crimeTypes", crimeTypes);
		}
		
		LOGGER.info("nationalities = " + nationalities);
		LOGGER.info("personIdTypes = " + personIdTypes);
		LOGGER.info("crimeTypes = " + crimeTypes);
		
		return ServiceResponse.success(null);
	}
}