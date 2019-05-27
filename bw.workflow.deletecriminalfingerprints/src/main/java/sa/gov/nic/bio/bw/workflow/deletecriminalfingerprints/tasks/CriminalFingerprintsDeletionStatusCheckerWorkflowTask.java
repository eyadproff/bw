package sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.webservice.CriminalFingerprintsDeletionAPI;

public class CriminalFingerprintsDeletionStatusCheckerWorkflowTask extends WorkflowTask
{
	public enum Status
	{
		PENDING,
		SUCCESS
	}
	
	@Input(alwaysRequired = true) private Long tcn;
	@Output private Status status;
	@Output private Boolean noFingerprintsFound;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(CriminalFingerprintsDeletionAPI.class);
		var apiCall = api.checkCriminalFingerprintsDeletionStatus(workflowId, workflowTcn, tcn);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		if(!taskResponse.isSuccess() && "B003-0057".equals(taskResponse.getErrorCode())) noFingerprintsFound = true;
		else
		{
			resetWorkflowStepIfNegativeTaskResponse(taskResponse);
			
			Integer httpCode = taskResponse.getHttpCode();
			if(httpCode == 200) status = Status.SUCCESS;
			else if(httpCode == 202) status = Status.PENDING;
		}
	}
}