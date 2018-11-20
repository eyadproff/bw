package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReportAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class GeneratingNewCivilBiometricsIdWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private Long personId;
	@Input(alwaysRequired = true) private Long civilBiometricsId;
	@Output	private Long criminalBiometricsId;
	
	@Override
	public void execute() throws Signal
	{
		ConvictedReportAPI convictedReportAPI = Context.getWebserviceManager().getApi(ConvictedReportAPI.class);
		Call<Long> apiCall = convictedReportAPI.generateGeneralFileNumber(personId, civilBiometricsId);
		TaskResponse<Long> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		criminalBiometricsId = taskResponse.getResult();
	}
}