package sa.gov.nic.bio.bw.workflow.registeriris.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.registeriris.webservice.IrisRegistrationAPI;

public class SubmitIrisRegistrationWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long personId;
	@Input(alwaysRequired = true) private String rightIrisBase64;
	@Input(alwaysRequired = true) private String leftIrisBase64;
	@Output private Long tcn;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(IrisRegistrationAPI.class);
		var apiCall = api.submitIrisRegistration(workflowId, workflowTcn, personId, rightIrisBase64, leftIrisBase64);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		tcn = taskResponse.getResult().getTcn();
	}
}