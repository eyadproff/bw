package sa.gov.nic.bio.bw.features.visaapplicantsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.features.visaapplicantsenrollment.workflow.VisaApplicantEnrollmentResponse;

public interface VisaApplicantsEnrollmentAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/enrollment/visa-applicant/v1")
	Call<VisaApplicantEnrollmentResponse> enrollVisaApplicant(@Field("visa-applicant-info") String visaApplicantInfo);
}