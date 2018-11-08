package sa.gov.nic.bio.bw.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.features.commons.webservice.FingerprintsByIdAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.List;

public class FetchingMissingFingerprintsWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long personId;
	@Output private List<Integer> missingFingerprints;
	
	public static TaskResponse<List<Integer>> execute(long personId)
	{
		FingerprintsByIdAPI fingerprintsByIdAPI = Context.getWebserviceManager().getApi(FingerprintsByIdAPI.class);
		Call<List<Integer>> apiCall = fingerprintsByIdAPI.getFingerprintAvailability(personId);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
	
	@Override
	public void execute() throws Signal
	{
		FingerprintsByIdAPI fingerprintsByIdAPI = Context.getWebserviceManager().getApi(FingerprintsByIdAPI.class);
		Call<List<Integer>> apiCall = fingerprintsByIdAPI.getFingerprintAvailability(personId);
		TaskResponse<List<Integer>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		missingFingerprints = new ArrayList<>();
		for(int i = 1; i <= 10; i++) missingFingerprints.add(i);
		taskResponse.getResult().forEach(missingFingerprints::remove);
	}
}