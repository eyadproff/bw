package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.webservice.DeadPersonRecordById;
import sa.gov.nic.bio.commons.TaskResponse;

public class DeadPersonRecordByIdWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private long recordId;
	@Output private DeadPersonRecord deadPersonRecord;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn) throws Signal, InterruptedException
	{
		DeadPersonRecordById deadPersonRecordById = Context.getWebserviceManager().getApi(DeadPersonRecordById.class);
		Call<DeadPersonRecord> apiCall = deadPersonRecordById.getDeadPersonRecordById(workflowId, workflowTcn,
		                                                                              recordId);
		TaskResponse<DeadPersonRecord> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		deadPersonRecord = taskResponse.getResult();
	}
}