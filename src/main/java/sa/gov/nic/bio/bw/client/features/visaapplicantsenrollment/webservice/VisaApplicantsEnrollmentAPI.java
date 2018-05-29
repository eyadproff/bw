package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow.VisaApplicantEnrollmentResponse;

public interface VisaApplicantsEnrollmentAPI
{
	@FormUrlEncoded
	@POST
	Call<VisaApplicantEnrollmentResponse> enrollVisaApplicant(@Url String url,
	                                                          @Field("visa-applicant-info") String visaApplicantInfo);
}