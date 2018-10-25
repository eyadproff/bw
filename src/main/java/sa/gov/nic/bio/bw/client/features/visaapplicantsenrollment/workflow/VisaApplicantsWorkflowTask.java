package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantInfo;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantsEnrollmentAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class VisaApplicantsWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private VisaApplicantInfo visaApplicantInfo;
	@Output private VisaApplicantEnrollmentResponse visaApplicantEnrollmentResponse;
	
	@Override
	public void execute() throws Signal
	{
		VisaApplicantsEnrollmentAPI visaApplicantsEnrollmentAPI =
											Context.getWebserviceManager().getApi(VisaApplicantsEnrollmentAPI.class);
		
		String visaApplicantInfoJson = new Gson().toJson(visaApplicantInfo,
		                                                 TypeToken.get(VisaApplicantInfo.class).getType());
		
		Call<VisaApplicantEnrollmentResponse> apiCall =
												visaApplicantsEnrollmentAPI.enrollVisaApplicant(visaApplicantInfoJson);
		TaskResponse<VisaApplicantEnrollmentResponse> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		visaApplicantEnrollmentResponse = taskResponse.getResult();
	}
}