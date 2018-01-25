package sa.gov.nic.bio.bw.client.features.mofaenrollment.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.workflow.ServiceBase;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice.VisaTypeBean;

import java.util.List;
import java.util.logging.Logger;

public class LookupService extends ServiceBase
{
	private static final Logger LOGGER = Logger.getLogger(LookupService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<NationalityBean> nationalities = (List<NationalityBean>) userSession.getAttribute("lookups.mofa.nationalities");
		
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) userSession.getAttribute("lookups.mofa.visaTypes");
		
		if(nationalities == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupNationalities");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<NationalityBean>> nationalitiesCall = lookupAPI.lookupNationalities(url);
			ApiResponse<List<NationalityBean>> nationalitiesResponse = Context.getWebserviceManager().executeApi(nationalitiesCall);
			
			if(nationalitiesResponse.isSuccess())
			{
				nationalities = nationalitiesResponse.getResult();
				nationalities.removeIf(nationalityBean -> nationalityBean.getMofaNationalityCode().trim().isEmpty());
			}
			else
			{
				bypassResponse(execution, nationalitiesResponse, true);
				return;
			}
			
			userSession.setAttribute("lookups.mofa.nationalities", nationalities);
		}
		
		if(visaTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupVisaTypes");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<VisaTypeBean>> visaTypesCall = lookupAPI.lookupVisaTypes(url);
			ApiResponse<List<VisaTypeBean>> visaTypesResponse = Context.getWebserviceManager().executeApi(visaTypesCall);
			
			if(visaTypesResponse.isSuccess()) visaTypes = visaTypesResponse.getResult();
			else
			{
				bypassResponse(execution, visaTypesResponse, true);
				return;
			}
			
			userSession.setAttribute("lookups.mofa.visaTypes", visaTypes);
		}
		
		LOGGER.info("nationalities = " + nationalities);
		LOGGER.info("visaTypes = " + visaTypes);
		
		bypassSuccessResponseWithNoResult(execution, false);
	}
}