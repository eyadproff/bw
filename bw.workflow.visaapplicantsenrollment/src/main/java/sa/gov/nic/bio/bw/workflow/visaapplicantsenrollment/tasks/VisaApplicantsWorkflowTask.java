package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.webservice.VisaApplicantInfo;
import sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.webservice.VisaApplicantsEnrollmentAPI;
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
		
		String visaApplicantInfoJson = AppUtils.toJson(visaApplicantInfo);
		
		Call<VisaApplicantEnrollmentResponse> apiCall =
												visaApplicantsEnrollmentAPI.enrollVisaApplicant(visaApplicantInfoJson);
		TaskResponse<VisaApplicantEnrollmentResponse> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		visaApplicantEnrollmentResponse = taskResponse.getResult();
	}
}