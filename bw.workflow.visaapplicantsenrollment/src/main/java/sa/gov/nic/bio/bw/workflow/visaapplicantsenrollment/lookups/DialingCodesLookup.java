package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.lookups;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.utils.VisaApplicantsEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans.CountryDialingCode;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class DialingCodesLookup implements Callable<TaskResponse<?>>, AppLogger
{
	public static final String KEY = "lookups.dialingCodes";
	private static final String DIALING_CODES_FILE =
										"/sa/gov/nic/bio/bw/workflow/visaapplicantsenrollment/data/dialing_codes.csv";
	
	@Override
	public TaskResponse<?> call()
	{
		@SuppressWarnings("unchecked")
		List<CountryDialingCode> dialingCodes = (List<CountryDialingCode>) Context.getUserSession().getAttribute(KEY);
		
		if(dialingCodes == null)
		{
			dialingCodes = new ArrayList<>();
			
			try
			{
				try(InputStream is = getClass().getResourceAsStream(DIALING_CODES_FILE))
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
				String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00001.getCode();
				String[] errorDetails = {"failed to read the dialing codes from the file!"};
				return TaskResponse.failure(errorCode, e, errorDetails);
			}
			
			Context.getUserSession().setAttribute(KEY, dialingCodes);
		}
		
		return TaskResponse.success();
	}
}