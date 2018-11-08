package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.HashMap;
import java.util.Map;

public interface WorkflowTask extends AppLogger
{
	default <T> void resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse<T> taskResponse) throws Signal
	{
		if(!taskResponse.isSuccess())
		{
			Map<String, Object> payload = new HashMap<>();
			payload.put(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
			throw new Signal(SignalType.RESET_WORKFLOW_STEP, payload);
		}
		
		if(taskResponse.getResult() == null)
		{
			Map<String, Object> payload = new HashMap<>();
			payload.put(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE,
			            TaskResponse.failure(CoreErrorCodes.C002_00027.getCode(),
			                                 new String[]{"Null response from the task: " + getClass().getName()}));
			throw new Signal(SignalType.RESET_WORKFLOW_STEP, payload);
		}
	}
	
	void execute() throws Signal;
}