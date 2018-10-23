package sa.gov.nic.bio.bw.client.features.commons.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.HashMap;
import java.util.Map;

@FxmlFile("lookup.fxml")
public class LookupFxController extends WizardStepFxControllerBase
{
	@FXML private ProgressIndicator progressIndicator;
	@FXML private Button btnTryAgain;
	
	@Override
	protected void initialize(){}
	
	@Override
	protected void onAttachedToScene()
	{
		submitTask();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(!newForm)
		{
			GuiUtils.showNode(progressIndicator, false);
			GuiUtils.showNode(btnTryAgain, true);
			
			TaskResponse<?> taskResponse = (TaskResponse<?>) uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			if(taskResponse != null && !taskResponse.isSuccess())
											 reportNegativeTaskResponse(taskResponse.getErrorCode(),
											                            taskResponse.getException(),
											                            taskResponse.getErrorDetails());
		}
	}
	
	@FXML
	private void onTryAgainButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(progressIndicator, true);
		GuiUtils.showNode(btnTryAgain, false);
		hideNotification();
		
		submitTask();
	}
	
	private void submitTask()
	{
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(new HashMap<>());
	}
}