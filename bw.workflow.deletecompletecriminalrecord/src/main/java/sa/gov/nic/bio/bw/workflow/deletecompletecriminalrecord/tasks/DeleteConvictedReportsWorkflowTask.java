package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.webservice.CriminalRecordDeletionAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class DeleteConvictedReportsWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long criminalBiometricsId;
	
	@Override
	public void execute() throws Signal
	{
		CriminalRecordDeletionAPI api = Context.getWebserviceManager().getApi(CriminalRecordDeletionAPI.class);
		Call<Void> apiCall = api.deleteConvictedReportsByCriminalBiometricsId(workflowId, workflowTcn,
		                                                                      criminalBiometricsId);
		TaskResponse<Void> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
	}
}