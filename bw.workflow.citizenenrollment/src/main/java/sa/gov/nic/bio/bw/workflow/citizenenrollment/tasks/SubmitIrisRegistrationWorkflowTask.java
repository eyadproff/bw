package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.CitizenEnrollmentInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.IrisRegistrationAPI;

public class SubmitIrisRegistrationWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true)
	private CitizenEnrollmentInfo citizenEnrollmentInfo;
	@Output private Long tcn;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(IrisRegistrationAPI.class);
		var apiCall = api.submitIrisRegistration(workflowId, workflowTcn, citizenEnrollmentInfo.getPersonId(),
				citizenEnrollmentInfo.getCapturedRightIrisBase64(), citizenEnrollmentInfo.getCapturedLeftIrisBase64());
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		tcn = taskResponse.getResult();
	}
}