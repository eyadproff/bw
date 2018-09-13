package sa.gov.nic.bio.bw.client.features.commons.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class SamisIdTypesLookup implements Callable<ServiceResponse<Void>>
{
	public static final String KEY = "lookups.samisIdTypes";
	private static final Logger LOGGER = Logger.getLogger(SamisIdTypesLookup.class.getName());
	
	@Override
	public ServiceResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<SamisIdType> samisIdTypes = (List<SamisIdType>) Context.getUserSession().getAttribute(KEY);
		
		if(samisIdTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<SamisIdType>> samisIdTypesCall = lookupAPI.lookupSamisIdTypes();
			ServiceResponse<List<SamisIdType>> samisIdTypesResponse = Context.getWebserviceManager()
																		     .executeApi(samisIdTypesCall);
			
			if(samisIdTypesResponse.isSuccess()) samisIdTypes = samisIdTypesResponse.getResult();
			else return ServiceResponse.failure(samisIdTypesResponse.getErrorCode(),
			                                    samisIdTypesResponse.getException(),
			                                    samisIdTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, samisIdTypes);
			LOGGER.info(KEY + " = " + samisIdTypes);
		}
		
		return ServiceResponse.success(null);
	}
}