package sa.gov.nic.bio.bw.client.features.mofaenrollment.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice.VisaTypeBean;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.logging.Logger;

public class LookupService
{
	private static final Logger LOGGER = Logger.getLogger(LookupService.class.getName());
	
	public static ServiceResponse<Void> execute()
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<NationalityBean> nationalities = (List<NationalityBean>)
														userSession.getAttribute("lookups.mofa.nationalities");
		
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) userSession.getAttribute("lookups.mofa.visaTypes");
		
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
			else // TODO
			{
				//
				//bypassResponse(execution, nationalitiesResponse, true);
				//return;
			}
			
			userSession.setAttribute("lookups.mofa.nationalities", nationalities);
		}
		
		if(visaTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupVisaTypes");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<VisaTypeBean>> visaTypesCall = lookupAPI.lookupVisaTypes(url);
			ServiceResponse<List<VisaTypeBean>> visaTypesResponse = Context.getWebserviceManager().executeApi(visaTypesCall);
			
			if(visaTypesResponse.isSuccess()) visaTypes = visaTypesResponse.getResult();
			else // TODO
			{
				//bypassResponse(execution, visaTypesResponse, true);
				//return;
			}
			
			userSession.setAttribute("lookups.mofa.visaTypes", visaTypes);
		}
		
		LOGGER.info("nationalities = " + nationalities);
		LOGGER.info("visaTypes = " + visaTypes);
		
		//bypassSuccessResponseWithNoResult(execution, false);
		return null;
	}
}