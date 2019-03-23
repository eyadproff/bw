package sa.gov.nic.bio.bw.workflow.deleteconvictedreport.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.deleteconvictedreport.webservice.ConvictedReportDeletionAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class DeleteConvictedReportWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long reportNumber;
	
	@Override
	public void execute() throws Signal
	{
		ConvictedReportDeletionAPI api = Context.getWebserviceManager().getApi(ConvictedReportDeletionAPI.class);
		Call<Void> call = api.deleteConvictedReportByReportNumber(workflowId, workflowTcn, reportNumber);
		TaskResponse<Void> taskResponse = Context.getWebserviceManager().executeApi(call);
		resetWorkflowStepIfNegativeTaskResponse(taskResponse);
	}
}