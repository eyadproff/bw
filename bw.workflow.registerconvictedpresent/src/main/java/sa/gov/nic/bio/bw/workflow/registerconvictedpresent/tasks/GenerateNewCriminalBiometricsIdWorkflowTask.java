package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.CriminalFingerprintsAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class GenerateNewCriminalBiometricsIdWorkflowTask extends WorkflowTask
{
	@Output private Long criminalBiometricsId;
	
	@Override
	public void execute() throws Signal
	{
		CriminalFingerprintsAPI api = Context.getWebserviceManager().getApi(CriminalFingerprintsAPI.class);
		Call<Long> apiCall = api.generateNewCriminalBiometricsID(workflowId, workflowTcn);
		TaskResponse<Long> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		criminalBiometricsId = taskResponse.getResult();
	}
}