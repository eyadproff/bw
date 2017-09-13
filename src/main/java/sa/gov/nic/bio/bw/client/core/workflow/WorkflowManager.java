package sa.gov.nic.bio.bw.client.core.workflow;

import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import sa.gov.nic.bio.bw.client.core.interfaces.UiProxy;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Fouad on 17-Jul-17.
 */
public class WorkflowManager
{
	private static final Logger LOGGER = Logger.getLogger(WorkflowManager.class.getName());
	private ProcessEngine processEngine;
	private RepositoryService repositoryService;
	private RuntimeService runtimeService;
	private IdentityService identityService;
	private TaskService taskService;
	private FormService formService;
	private ProcessInstance processInstance;
	private String currentTaskId;
	
	public void load(List<String> workflowFilePaths)
	{
		ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
		processEngine = configuration.buildProcessEngine();
		
		repositoryService = processEngine.getRepositoryService();
		runtimeService = processEngine.getRuntimeService();
		identityService = processEngine.getIdentityService();
		taskService = processEngine.getTaskService();
		formService = processEngine.getFormService();
		
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
		
		LOGGER.info("Deploying the following workflows:");
		for(String workflowFilePath : workflowFilePaths)
		{
			deploymentBuilder.addClasspathResource(workflowFilePath);
			LOGGER.info(workflowFilePath);
		}
		
		deploymentBuilder.deploy();
		LOGGER.info("Deployment is completed successfully");
		
		processInstance = runtimeService.startProcessInstanceByKey("coreProcess");
		LOGGER.info("The workflow process \"" + processInstance.getProcessDefinitionName() + "\" is started");
	}
	
	public void startProcess(UiProxy uiProxy)
	{
		executeUserTask(uiProxy);
	}
	
	public void submitFormTask(Map<String, String> uiDataMap, UiProxy uiProxy)
	{
		formService.submitTaskFormData(currentTaskId, uiDataMap); // executes as taskService.complete(taskId)
		executeUserTask(uiProxy);
	}
	
	public void raiseSignalEvent(String menuId)
	{
		/*runtimeService.createExecutionQuery()
				.list().stream().peek(e -> System.out.println(e.getId())).forEach(e -> runtimeService.signalEventReceived("The Signal", e.getId()));*/
		//runtimeService.setVariable(execution.getId(), "menuId", menuId);
		//runtimeService.signalEventReceived("The Signal");
	}
	
	private void executeUserTask(UiProxy uiProxy)
	{
		Task task = taskService.createTaskQuery().includeProcessVariables().singleResult();
		
		if(task != null)
		{
			currentTaskId = task.getId();
			Map<String, Object> processVariables = task.getProcessVariables();
			String formKey = formService.getTaskFormData(currentTaskId).getFormKey();
			uiProxy.showForm(formKey, processVariables);
		}
		else // no task? that means the end of workflow? should never happen
		{
			// TODO: report for error
		}
	}
}