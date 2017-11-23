package sa.gov.nic.bio.bw.client.core.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.bpmn.behavior.CallActivityBehavior;
import org.activiti.engine.impl.context.Context;

import java.util.logging.Logger;

public class MenuWorkflowService implements JavaDelegate
{
	private static final Logger LOGGER = Logger.getLogger(MenuWorkflowService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		//CallActivityBehavior callActivityBehavior = new CallActivityBehavior()
	}
}