package sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.webservice.FingerprintsAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RetrieveFingerprintsAvailabilityByCriminalBiometricIdWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long criminalBiometricsId;
	@Output private List<Integer> missingFingerprints;
	
	@Override
	public void execute() throws Signal
	{
		FingerprintsAPI api = Context.getWebserviceManager().getApi(FingerprintsAPI.class);
		Call<List<Integer>> call = api.getFingerprintsAvailabilityByCriminalBiometricsId(workflowId, workflowTcn,
		                                                                                 criminalBiometricsId);
		TaskResponse<List<Integer>> taskResponse = Context.getWebserviceManager().executeApi(call);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		List<Integer> availableFingerprints = taskResponse.getResult();
		
		missingFingerprints = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
		missingFingerprints.removeAll(availableFingerprints);
	}
}