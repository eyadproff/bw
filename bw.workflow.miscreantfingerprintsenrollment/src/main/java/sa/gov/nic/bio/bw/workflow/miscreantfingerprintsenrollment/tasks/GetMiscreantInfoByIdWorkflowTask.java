package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.beans.MiscreantInfo;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.webservice.MiscreantInfoByIdAPI;

public class GetMiscreantInfoByIdWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long miscreantId;
	@Output private MiscreantInfo miscreantInfo;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(MiscreantInfoByIdAPI.class);
		var apiCall = api.getMiscreantInfoById(workflowId, workflowTcn, miscreantId);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		miscreantInfo = taskResponse.getResult();
	}
}