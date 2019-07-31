package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.webservice.PersonInfoByIdAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class GetDeporteeInfoByIdWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long deporteeId;
	@Input private Boolean returnNullResultInCaseNotFound;
	@Output private DeporteeInfo deporteeInfo;
	
	@Override
	public void execute() throws Signal
	{
		PersonInfoByIdAPI personInfoByIdAPI = Context.getWebserviceManager().getApi(PersonInfoByIdAPI.class);
		Call<DeporteeInfo> apiCall = personInfoByIdAPI.getDeporteeInfoById(workflowId, workflowTcn, deporteeId);
		TaskResponse<DeporteeInfo> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		boolean notFound = !taskResponse.isSuccess() && "B004-00017".equals(taskResponse.getErrorCode());
		
		if(returnNullResultInCaseNotFound != null && returnNullResultInCaseNotFound && notFound) return;
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		deporteeInfo = taskResponse.getResult();

		if(deporteeInfo.getSamisId() == null || deporteeInfo.getSamisId() == 0L) deporteeInfo.setSamisId(deporteeId);
	}
}