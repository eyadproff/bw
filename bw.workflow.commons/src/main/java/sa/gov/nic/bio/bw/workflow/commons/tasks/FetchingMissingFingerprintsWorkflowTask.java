package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FingerprintsByIdAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.List;

public class FetchingMissingFingerprintsWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long personId;
	@Output private List<Integer> missingFingerprints;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn) throws Signal, InterruptedException
	{
		FingerprintsByIdAPI fingerprintsByIdAPI = Context.getWebserviceManager().getApi(FingerprintsByIdAPI.class);
		Call<List<Integer>> apiCall = fingerprintsByIdAPI.getFingerprintAvailability(workflowId, workflowTcn, personId);
		TaskResponse<List<Integer>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		missingFingerprints = new ArrayList<>();
		for(int i = 1; i <= 10; i++) missingFingerprints.add(i);
		taskResponse.getResult().forEach(missingFingerprints::remove);
	}
}