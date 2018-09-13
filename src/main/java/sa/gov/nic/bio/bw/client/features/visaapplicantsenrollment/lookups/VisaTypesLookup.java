package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaTypeBean;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class VisaTypesLookup implements Callable<ServiceResponse<Void>>
{
	public static final String KEY = "lookups.visaTypes";
	private static final Logger LOGGER = Logger.getLogger(VisaTypesLookup.class.getName());
	
	@Override
	public ServiceResponse<Void> call()
	{
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) Context.getUserSession().getAttribute(KEY);
		
		if(visaTypes == null)
		{
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<VisaTypeBean>> visaTypesCall = lookupAPI.lookupVisaTypes();
			ServiceResponse<List<VisaTypeBean>> visaTypesResponse = Context.getWebserviceManager()
																		   .executeApi(visaTypesCall);
			
			if(visaTypesResponse.isSuccess()) visaTypes = visaTypesResponse.getResult();
			else return ServiceResponse.failure(visaTypesResponse.getErrorCode(),
			                                    visaTypesResponse.getException(),
			                                    visaTypesResponse.getErrorDetails());
			
			Context.getUserSession().setAttribute(KEY, visaTypes);
			LOGGER.info(KEY + " = " + visaTypes);
		}
		
		return ServiceResponse.success(null);
	}
}