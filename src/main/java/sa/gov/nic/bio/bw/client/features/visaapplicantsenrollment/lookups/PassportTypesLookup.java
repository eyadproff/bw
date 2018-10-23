package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class PassportTypesLookup implements Callable<TaskResponse<Void>>, AppLogger
{
	public static final String KEY = "lookups.passportTypes";
	
	@Override
	public TaskResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<PassportTypeBean> passportTypes = (List<PassportTypeBean>) Context.getUserSession().getAttribute(KEY);
		
		if(passportTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<PassportTypeBean>> passportTypesCall = lookupAPI.lookupPassportTypes();
			TaskResponse<List<PassportTypeBean>> passportTypesResponse = Context.getWebserviceManager()
																				   .executeApi(passportTypesCall);
			
			if(passportTypesResponse.isSuccess()) passportTypes = passportTypesResponse.getResult();
			else return TaskResponse.failure(passportTypesResponse.getErrorCode(),
			                                 passportTypesResponse.getException(),
			                                 passportTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, passportTypes);
			LOGGER.info(KEY + " = " + passportTypes);
		}
		
		return TaskResponse.success(null);
	}
}