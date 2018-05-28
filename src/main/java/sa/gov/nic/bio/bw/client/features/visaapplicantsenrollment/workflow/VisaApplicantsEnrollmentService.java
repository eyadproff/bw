package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantsEnrollmentAPI;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantInfo;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class VisaApplicantsEnrollmentService
{
	public static ServiceResponse<Long> execute(VisaApplicantInfo visaApplicantInfo)
	{
		VisaApplicantsEnrollmentAPI visaApplicantsEnrollmentAPI =
											Context.getWebserviceManager().getApi(VisaApplicantsEnrollmentAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.visaApplicantsEnrollment");
		
		String visaApplicantInfoJson = new Gson().toJson(visaApplicantInfo, TypeToken.get(VisaApplicantInfo.class).getType());
		
		Call<Long> apiCall = visaApplicantsEnrollmentAPI.enrollVisaApplicant(url, visaApplicantInfoJson);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}