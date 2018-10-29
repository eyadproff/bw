package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintsByIdAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class FetchingFingerprintsWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long personId;
	@Output private List<Finger> fingerprints;
	
	@Override
	public void execute() throws Signal
	{
		FingerprintsByIdAPI fingerprintsByIdAPI = Context.getWebserviceManager().getApi(FingerprintsByIdAPI.class);
		Call<List<Finger>> apiCall = fingerprintsByIdAPI.getFingerprintsById(personId);
		TaskResponse<List<Finger>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		fingerprints = taskResponse.getResult();
	}
}