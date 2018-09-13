package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class CrimeTypesLookup implements Callable<ServiceResponse<Void>>
{
	public static final String KEY = "lookups.crimeTypes";
	private static final Logger LOGGER = Logger.getLogger(CrimeTypesLookup.class.getName());
	
	@Override
	public ServiceResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(KEY);
		
		if(crimeTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CrimeType>> crimeTypesCall = lookupAPI.lookupCrimeTypes();
			ServiceResponse<List<CrimeType>> crimeTypesResponse = Context.getWebserviceManager()
																		 .executeApi(crimeTypesCall);
			
			if(crimeTypesResponse.isSuccess()) crimeTypes = crimeTypesResponse.getResult();
			else return ServiceResponse.failure(crimeTypesResponse.getErrorCode(),
			                                    crimeTypesResponse.getException(),
			                                    crimeTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, crimeTypes);
			LOGGER.info(KEY + " = " + crimeTypes);
		}
		
		return ServiceResponse.success(null);
	}
}