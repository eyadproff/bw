package sa.gov.nic.bio.bw.client.core.workflow;

import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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
	private RepositoryService repositoryService;
	private RuntimeService runtimeService;
	private IdentityService identityService;
	private TaskService taskService;
	private FormService formService;
	
	public void load(List<String> workflowFilePaths)
	{
		ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
		ProcessEngine processEngine = configuration.buildProcessEngine();
		
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
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("coreProcess");
		LOGGER.info("The workflow process \"" + processInstance.getProcessDefinitionName() + "\" is started");
	}
	
	public void startProcess(UiProxy uiProxy)
	{
		executeUserTask(uiProxy);
	}
	
	public void submitFormTask(String taskId, Map<String, String> uiDataMap, UiProxy uiProxy)
	{
		formService.submitTaskFormData(taskId, uiDataMap); // executes as taskService.complete(taskId)
		executeUserTask(uiProxy);
	}
	
	private void executeUserTask(UiProxy uiProxy)
	{
		Task task = taskService.createTaskQuery().includeProcessVariables().singleResult();
		
		if(task != null)
		{
			String taskId = task.getId();
			Map<String, Object> processVariables = task.getProcessVariables();
			String formKey = formService.getTaskFormData(taskId).getFormKey();
			uiProxy.showForm(formKey, taskId, processVariables);
		}
		else // no task? that means the end of workflow? should never happen
		{
			// TODO: report for error
		}
	}
}