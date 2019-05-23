package sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.webservice.CriminalFingerprintsDeletionAPI;

public class SubmitCriminalFingerprintsDeletionWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long criminalBiometricsId;
	@Output private Long tcn;
	@Output private Boolean noFingerprintsFound;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(CriminalFingerprintsDeletionAPI.class);
		var apiCall = api.deleteCriminalFingerprints(workflowId, workflowTcn, criminalBiometricsId);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		if(!taskResponse.isSuccess() && "B003-0051".equals(taskResponse.getErrorCode())) noFingerprintsFound = true;
		else
		{
			resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
			tcn = taskResponse.getResult().getTcn();
		}
	}
}