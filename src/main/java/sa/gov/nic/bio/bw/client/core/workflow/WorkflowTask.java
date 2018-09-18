package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public interface WorkflowTask
{
	ServiceResponse<?> execute();
}