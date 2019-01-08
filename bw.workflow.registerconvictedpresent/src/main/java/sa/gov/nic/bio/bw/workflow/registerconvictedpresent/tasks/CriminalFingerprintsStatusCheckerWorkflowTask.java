package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.CriminalFingerprintsAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class CriminalFingerprintsStatusCheckerWorkflowTask extends WorkflowTask
{
	public enum Status
	{
		PENDING,
		SUCCESS
	}
	
	@Input(alwaysRequired = true) private Long tcn;
	@Output private Status status;
	
	@Override
	public void execute() throws Signal
	{
		CriminalFingerprintsAPI api = Context.getWebserviceManager().getApi(CriminalFingerprintsAPI.class);
		Call<Void> apiCall = api.checkCriminalFingerprintsStatus(workflowId, workflowTcn, tcn);
		TaskResponse<Void> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		Integer httpCode = taskResponse.getHttpCode();
		if(httpCode == 200) status = Status.SUCCESS;
		else if(httpCode == 202) status = Status.PENDING;
	}
}