package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class PassportTypesLookup implements Callable<ServiceResponse<Void>>
{
	public static final String KEY = "lookups.passportTypes";
	private static final Logger LOGGER = Logger.getLogger(PassportTypesLookup.class.getName());
	
	@Override
	public ServiceResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<PassportTypeBean> passportTypes = (List<PassportTypeBean>) Context.getUserSession().getAttribute(KEY);
		
		if(passportTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PassportTypeBean>> passportTypesCall = lookupAPI.lookupPassportTypes();
			ServiceResponse<List<PassportTypeBean>> passportTypesResponse = Context.getWebserviceManager()
																				   .executeApi(passportTypesCall);
			
			if(passportTypesResponse.isSuccess()) passportTypes = passportTypesResponse.getResult();
			else return ServiceResponse.failure(passportTypesResponse.getErrorCode(),
			                                    passportTypesResponse.getException(),
			                                    passportTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, passportTypes);
			LOGGER.info(KEY + " = " + passportTypes);
		}
		
		return ServiceResponse.success(null);
	}
}