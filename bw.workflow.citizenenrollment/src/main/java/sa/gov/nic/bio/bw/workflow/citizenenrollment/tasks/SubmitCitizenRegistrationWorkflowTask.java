package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.CitizenEnrollmentAPI;

public class SubmitCitizenRegistrationWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long personId;
	@Input(alwaysRequired = true) private String rightIrisBase64;
	@Input(alwaysRequired = true) private String leftIrisBase64;
	@Output private Long tcn;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(CitizenEnrollmentAPI.class);
		var apiCall = api.submitCitizenRegistration(workflowId, workflowTcn, personId, rightIrisBase64, leftIrisBase64);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		tcn = taskResponse.getResult();
	}
}