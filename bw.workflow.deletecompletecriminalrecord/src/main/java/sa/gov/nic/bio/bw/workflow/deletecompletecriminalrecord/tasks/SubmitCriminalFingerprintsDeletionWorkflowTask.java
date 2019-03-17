package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.beans.CriminalFingerprintsDeletionResponse;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.webservice.CriminalRecordDeletionAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class SubmitCriminalFingerprintsDeletionWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long criminalBiometricsId;
	@Output private Long tcn;
	
	@Override
	public void execute() throws Signal
	{
		CriminalRecordDeletionAPI api = Context.getWebserviceManager().getApi(CriminalRecordDeletionAPI.class);
		Call<CriminalFingerprintsDeletionResponse> apiCall = api.deleteCriminalFingerprints(workflowId, workflowTcn,
		                                                                                    criminalBiometricsId);
		TaskResponse<CriminalFingerprintsDeletionResponse> taskResponse = Context.getWebserviceManager()
																				 .executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		tcn = taskResponse.getResult().getTcn();
	}
}