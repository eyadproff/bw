package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface VisaApplicantsEnrollmentAPI
{
	@FormUrlEncoded
	@POST
	Call<Long> enrollVisaApplicant(@Url String url, @Field("foreign-info") String visaApplicantInfo);
}