package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans.VisaApplicantEnrollmentResponse;

public interface VisaApplicantsEnrollmentAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/enrollment/visa-applicant/v1")
	Call<VisaApplicantEnrollmentResponse> enrollVisaApplicant(@Header("Workflow-Code") Integer workflowId,
	                                                          @Header("Workflow-Tcn") Long workflowTcn,
	                                                          @Field("visa-applicant-info") String visaApplicantInfo);
}