package sa.gov.nic.bio.bw.client.core.workflow;

import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import sa.gov.nic.bio.bw.client.core.interfaces.UiProxy;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	private ProcessInstance coreProcessInstance;
	private String currentTaskId;
	
	public void load(List<String> workflowFilePaths, RuntimeEnvironment runtimeEnvironment) throws FileNotFoundException
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
			if(runtimeEnvironment == null || runtimeEnvironment == RuntimeEnvironment.DEV)
			{
				deploymentBuilder.addInputStream(workflowFilePath.substring(workflowFilePath.lastIndexOf("/")), new FileInputStream(workflowFilePath));
			}
			else deploymentBuilder.addClasspathResource(workflowFilePath);
			
			LOGGER.info(workflowFilePath);
		}
		
		deploymentBuilder.deploy();
		LOGGER.info("Deployment is completed successfully");
		
		coreProcessInstance = runtimeService.startProcessInstanceByKey("coreProcess");
		LOGGER.info("The workflow process \"" + coreProcessInstance.getProcessDefinitionName() + "\" is started");
	}
	
	public void startProcess(UiProxy uiProxy)
	{
		showPendingUiTask(uiProxy);
	}
	
	public void submitFormTask(Map<String, String> uiDataMap, UiProxy uiProxy)
	{
		formService.submitTaskFormData(currentTaskId, uiDataMap); // executes as taskService.complete(taskId)
		showPendingUiTask(uiProxy);
	}
	
	public void raiseSignalEvent(String signalName, Map<String, Object> variables, UiProxy uiProxy)
	{
		runtimeService.signalEventReceived(signalName, variables);
		showPendingUiTask(uiProxy);
	}
	
	private void showPendingUiTask(UiProxy uiProxy)
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
			LOGGER.severe("No pending tasks in the workflow manager! That should never happen!");
		}
	}
}