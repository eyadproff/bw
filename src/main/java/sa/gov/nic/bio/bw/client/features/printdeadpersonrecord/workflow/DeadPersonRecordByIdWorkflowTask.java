package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecordById;
import sa.gov.nic.bio.commons.TaskResponse;

public class DeadPersonRecordByIdWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long recordId;
	@Output private DeadPersonRecord deadPersonRecord;
	
	@Override
	public void execute() throws Signal
	{
		DeadPersonRecordById deadPersonRecordById = Context.getWebserviceManager().getApi(DeadPersonRecordById.class);
		Call<DeadPersonRecord> apiCall = deadPersonRecordById.getDeadPersonRecordById(recordId);
		TaskResponse<DeadPersonRecord> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		deadPersonRecord = taskResponse.getResult();
	}
}