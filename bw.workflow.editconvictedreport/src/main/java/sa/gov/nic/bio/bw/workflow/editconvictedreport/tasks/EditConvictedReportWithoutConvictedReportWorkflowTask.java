package sa.gov.nic.bio.bw.workflow.editconvictedreport.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.webservice.EditConvictedReportAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class EditConvictedReportWithoutConvictedReportWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private ConvictedReport convictedReport;
	@Output private Long newReportNumber;
	
	@Override
	public void execute() throws Signal
	{
		EditConvictedReportAPI api = Context.getWebserviceManager().getApi(EditConvictedReportAPI.class);
		String convictedReportJson = AppUtils.toJson(convictedReport);
		Call<Long> apiCall = api.editConvictedReportWithoutPersonInfo(workflowId, workflowTcn, convictedReportJson);
		TaskResponse<Long> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		newReportNumber = taskResponse.getResult();
	}
}