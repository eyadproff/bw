package sa.gov.nic.bio.bw.client.features.foreignenrollment.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.CountryDialingCode;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.VisaTypeBean;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.VisaTypesLookupAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class LookupService
{
	private static final Logger LOGGER = Logger.getLogger(LookupService.class.getName());
	private static final String DIALING_CODES_FILE =
										"sa/gov/nic/bio/bw/client/features/foreignenrollment/data/dialing_codes.csv";
	
	public static ServiceResponse<Void> execute()
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<NationalityBean> nationalities = (List<NationalityBean>)
															userSession.getAttribute("lookups.nationalities");
		
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) userSession.getAttribute("lookups.visaTypes");
		
		@SuppressWarnings("unchecked")
		List<CountryDialingCode> dialingCodes = (List<CountryDialingCode>)
															userSession.getAttribute("lookups.dialingCodes");
		
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
		
		if(visaTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupVisaTypes");
			VisaTypesLookupAPI visaTypesLookupAPI = Context.getWebserviceManager().getApi(VisaTypesLookupAPI.class);
			Call<List<VisaTypeBean>> visaTypesCall = visaTypesLookupAPI.lookupVisaTypes(url);
			ServiceResponse<List<VisaTypeBean>> visaTypesResponse = Context.getWebserviceManager()
																		   .executeApi(visaTypesCall);
			
			if(visaTypesResponse.isSuccess()) visaTypes = visaTypesResponse.getResult();
			else return ServiceResponse.failure(visaTypesResponse.getErrorCode(),
			                                    visaTypesResponse.getException(),
			                                    visaTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.visaTypes", visaTypes);
		}
		
		if(dialingCodes == null)
		{
			dialingCodes = new ArrayList<>();
			
			try
			{
				URL resource = Thread.currentThread().getContextClassLoader().getResource(DIALING_CODES_FILE);
				List<String> lines = Files.readAllLines(Paths.get(Objects.requireNonNull(resource).toURI()));
				
				for(String line : lines)
				{
					String[] parts = line.split(",");
					String isoAlpha3Code = parts[0];
					int dialingCode = Integer.parseInt(parts[1]);
					String countryArabicName = parts[2];
					String countryEnglishName = parts[3];
					
					CountryDialingCode cdc = new CountryDialingCode(isoAlpha3Code, dialingCode, countryArabicName,
					                                                countryEnglishName);
					dialingCodes.add(cdc);
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				// TODO: report the error
			}
			
			userSession.setAttribute("lookups.dialingCodes", dialingCodes);
		}
		
		LOGGER.info("nationalities = " + nationalities);
		LOGGER.info("visaTypes = " + visaTypes);
		LOGGER.info("dialingCodes = " + dialingCodes);
		
		return ServiceResponse.success(null);
	}
}