package sa.gov.nic.bio.bw.client.features.foreignenrollment.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.LookupAPI;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.utils.ForeignEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.CountryDialingCode;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.MofaLookupAPI;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.VisaTypeBean;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LookupService
{
	private static final Logger LOGGER = Logger.getLogger(LookupService.class.getName());
	private static final String DIALING_CODES_FILE =
										"sa/gov/nic/bio/bw/client/features/foreignenrollment/data/dialing_codes.csv";
	
	public static ServiceResponse<Void> execute()
	{
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<CountryBean> countries = (List<CountryBean>) userSession.getAttribute("lookups.countries");
		
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) userSession.getAttribute("lookups.visaTypes");
		
		@SuppressWarnings("unchecked")
		List<PassportTypeBean> passportTypes = (List<PassportTypeBean>)
														userSession.getAttribute("lookups.passportTypes");
		
		@SuppressWarnings("unchecked")
		List<CountryDialingCode> dialingCodes = (List<CountryDialingCode>)
															userSession.getAttribute("lookups.dialingCodes");
		
		if(countries == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupNationalities");
			LookupAPI lookupAPI = Context.getWebserviceManager().getApi(LookupAPI.class);
			Call<List<CountryBean>> nationalitiesCall = lookupAPI.lookupNationalities(url);
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
		
		if(visaTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupVisaTypes");
			MofaLookupAPI mofaLookupAPI = Context.getWebserviceManager().getApi(MofaLookupAPI.class);
			Call<List<VisaTypeBean>> visaTypesCall = mofaLookupAPI.lookupVisaTypes(url);
			ServiceResponse<List<VisaTypeBean>> visaTypesResponse = Context.getWebserviceManager()
																		   .executeApi(visaTypesCall);
			
			if(visaTypesResponse.isSuccess()) visaTypes = visaTypesResponse.getResult();
			else return ServiceResponse.failure(visaTypesResponse.getErrorCode(),
			                                    visaTypesResponse.getException(),
			                                    visaTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.visaTypes", visaTypes);
		}
		
		if(passportTypes == null)
		{
			String url = System.getProperty("jnlp.bio.bw.service.lookupPassportTypes");
			MofaLookupAPI mofaLookupAPI = Context.getWebserviceManager().getApi(MofaLookupAPI.class);
			Call<List<PassportTypeBean>> passportTypesCall = mofaLookupAPI.lookupPassportTypes(url);
			ServiceResponse<List<PassportTypeBean>> passportTypesResponse = Context.getWebserviceManager()
																				   .executeApi(passportTypesCall);
			
			if(passportTypesResponse.isSuccess()) passportTypes = passportTypesResponse.getResult();
			else return ServiceResponse.failure(passportTypesResponse.getErrorCode(),
			                                    passportTypesResponse.getException(),
			                                    passportTypesResponse.getErrorDetails());
			
			userSession.setAttribute("lookups.passportTypes", passportTypes);
		}
		
		if(dialingCodes == null)
		{
			dialingCodes = new ArrayList<>();
			
			try
			{
				try(InputStream is = Thread.currentThread().getContextClassLoader()
										   .getResourceAsStream(DIALING_CODES_FILE))
				{
					List<String> lines = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
																				.lines().collect(Collectors.toList());
					
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
			}
			catch(Exception e)
			{
				String errorCode = ForeignEnrollmentErrorCodes.C010_00002.getCode();
				String[] errorDetails = {"failed to read the dialing codes from the file!"};
				return ServiceResponse.failure(errorCode, e, errorDetails);
			}
			
			userSession.setAttribute("lookups.dialingCodes", dialingCodes);
		}
		
		LOGGER.info("countries = " + countries);
		LOGGER.info("visaTypes = " + visaTypes);
		LOGGER.info("passportTypes = " + passportTypes);
		LOGGER.info("dialingCodes = " + dialingCodes);
		
		return ServiceResponse.success(null);
	}
}