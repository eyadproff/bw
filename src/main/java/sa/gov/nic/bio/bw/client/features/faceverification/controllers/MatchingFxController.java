package sa.gov.nic.bio.bw.client.features.faceverification.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.commons.TaskResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MatchingFxController extends WizardStepFxControllerBase
{
	@FXML private ProgressIndicator piProgress;
	@FXML private Label txtProgress;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/matching.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			showProgress(true);
			continueWorkflow();
		}
		else
		{
			showProgress(false);
			
			@SuppressWarnings("unchecked") TaskResponse<PersonInfo> response = (TaskResponse<PersonInfo>)
															uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			
			if(response != null) // there is a result
			{
				if(response.isSuccess()) goNext();
				else reportNegativeTaskResponse(response.getErrorCode(), response.getException(),
				                                response.getErrorDetails());
			}
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		showProgress(true);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, null);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	private void showProgress(boolean bShow)
	{
		GuiUtils.showNode(btnRetry, !bShow);
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(txtProgress, bShow);
		btnStartOver.setDisable(bShow);
	}
}