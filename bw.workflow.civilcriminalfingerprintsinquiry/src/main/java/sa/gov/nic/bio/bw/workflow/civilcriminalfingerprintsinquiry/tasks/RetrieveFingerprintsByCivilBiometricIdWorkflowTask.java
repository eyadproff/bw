package sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.webservice.FingerprintsAPI;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.List;

public class RetrieveFingerprintsByCivilBiometricIdWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long civilBiometricsId;
	
	@Output private List<Finger> fingerprints;
	@Output private List<Integer> missingFingerprints;
	
	@Override
	public void execute() throws Signal
	{
		FingerprintsAPI api = Context.getWebserviceManager().getApi(FingerprintsAPI.class);
		Call<List<Finger>> call = api.getFingerprintsByCivilBiometricsId(workflowId, workflowTcn, civilBiometricsId);
		TaskResponse<List<Finger>> taskResponse = Context.getWebserviceManager().executeApi(call);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		fingerprints = taskResponse.getResult();
		missingFingerprints = new ArrayList<>(); // TODO: temp
	}
}