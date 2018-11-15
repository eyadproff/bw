package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.tasks.VisaApplicantEnrollmentResponse;

public interface VisaApplicantsEnrollmentAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/enrollment/visa-applicant/v1")
	Call<VisaApplicantEnrollmentResponse> enrollVisaApplicant(@Field("visa-applicant-info") String visaApplicantInfo);
}