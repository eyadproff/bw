package sa.gov.nic.bio.bw.features.visaapplicantsenrollment.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.features.visaapplicantsenrollment.webservice.LookupAPI;
import sa.gov.nic.bio.bw.features.visaapplicantsenrollment.webservice.VisaTypeBean;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.concurrent.Callable;

public class VisaTypesLookup implements Callable<TaskResponse<Void>>, AppLogger
{
	public static final String KEY = "lookups.visaTypes";
	
	@Override
	public TaskResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) Context.getUserSession().getAttribute(KEY);
		
		if(visaTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<VisaTypeBean>> visaTypesCall = lookupAPI.lookupVisaTypes();
			TaskResponse<List<VisaTypeBean>> visaTypesResponse = Context.getWebserviceManager()
																		   .executeApi(visaTypesCall);
			
			if(visaTypesResponse.isSuccess()) visaTypes = visaTypesResponse.getResult();
			else return TaskResponse.failure(visaTypesResponse.getErrorCode(),
			                                 visaTypesResponse.getException(),
			                                 visaTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, visaTypes);
			LOGGER.info(KEY + " = " + visaTypes);
		}
		
		return TaskResponse.success(null);
	}
}