package sa.gov.nic.bio.bw.workflow.irisinquiry.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.irisinquiry.webservice.IrisInquiryAPI;

public class IrisInquiryWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) protected String rightIrisBase64;
	@Input(alwaysRequired = true) protected String leftIrisBase64;
	@Output private Long tcn;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(IrisInquiryAPI.class);
		var apiCall = api.inquireWithIris(workflowId, workflowTcn, rightIrisBase64, leftIrisBase64);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		tcn = taskResponse.getResult();
	}
}