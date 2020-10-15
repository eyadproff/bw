package sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FingerprintsByIdAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class FetchingFingerprintsWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long personId;
	@Output private List<Finger> fingerprints;
	
	@Override
	public void execute() throws Signal
	{
		FingerprintsByIdAPI fingerprintsByIdAPI = Context.getWebserviceManager().getApi(FingerprintsByIdAPI.class);
		Call<List<Finger>> apiCall = fingerprintsByIdAPI.getFingerprintsById(workflowId, workflowTcn, personId);
		TaskResponse<List<Finger>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		//no fingers found for this id
		if(!taskResponse.isSuccess() && "B003-0007".equals(taskResponse.getErrorCode())) return;

		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		fingerprints = taskResponse.getResult();
	}
}