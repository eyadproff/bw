package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
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
		
		@SuppressWarnings("unchecked") List<SamisIdType> samisIdTypes = (List<SamisIdType>)
															userSession.getAttribute("lookups.samisIdTypes");
		
		@SuppressWarnings("unchecked") List<DocumentType> documentTypes = (List<DocumentType>)
															userSession.getAttribute("lookups.documentTypes");
		
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
		
		if(samisIdTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<SamisIdType>> samisIdTypesCall = lookupAPI.lookupPersonIdTypes();
			ServiceResponse<List<SamisIdType>> samisIdTypesResponse = Context.getWebserviceManager()
					.executeApi(samisIdTypesCall);
			
			if(samisIdTypesResponse.isSuccess()) samisIdTypes = samisIdTypesResponse.getResult();
			else return ServiceResponse.failure(samisIdTypesResponse.getErrorCode(),
			                                    samisIdTypesResponse.getException(),
			                                    samisIdTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.samisIdTypes", samisIdTypes);
			LOGGER.info("samisIdTypes = " + samisIdTypes);
		}
		
		if(documentTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<DocumentType>> documentTypesCall = lookupAPI.lookupIdTypes();
			ServiceResponse<List<DocumentType>> documentTypesResponse =
														Context.getWebserviceManager().executeApi(documentTypesCall);
			
			if(documentTypesResponse.isSuccess()) documentTypes = documentTypesResponse.getResult();
			else return ServiceResponse.failure(documentTypesResponse.getErrorCode(),
			                                    documentTypesResponse.getException(),
			                                    documentTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.documentTypes", documentTypes);
			LOGGER.info("documentTypes = " + documentTypes);
		}
		
		LOGGER.info("countries = " + countries);
		
		return ServiceResponse.success(null);
	}
}