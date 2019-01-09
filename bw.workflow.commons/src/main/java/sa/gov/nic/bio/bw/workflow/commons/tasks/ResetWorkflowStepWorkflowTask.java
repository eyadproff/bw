package sa.gov.nic.bio.bw.workflow.commons.tasks;

import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.commons.TaskResponse;

public class ResetWorkflowStepWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private TaskResponse<?> taskResponse;
	
	@Override
	public void execute() throws Signal
	{
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
	}
}