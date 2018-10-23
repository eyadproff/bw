package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.commons.TaskResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchingPersonInfoPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_DEVICES_RUNNER_IS_RUNNING = "DEVICES_RUNNER_IS_RUNNING";
	public static final String KEY_RETRY_PERSON_INFO_FETCHING = "RETRY_PERSON_INFO_FETCHING";
	
	@FXML private VBox paneError;
	@FXML private VBox paneDevicesRunnerNotRunning;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblProgress;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/fetchingPersonInfo.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(actionEvent ->
		{
		    hideNotification();
		    startOver();
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
			deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
			{
			    GuiUtils.showNode(piProgress, running);
			    GuiUtils.showNode(lblProgress, running);
			    GuiUtils.showNode(paneDevicesRunnerNotRunning, !running);
			    GuiUtils.showNode(btnStartOver, !running);
			
			    Map<String, Object> uiDataMap = new HashMap<>();
			    uiDataMap.put(KEY_DEVICES_RUNNER_IS_RUNNING, running);
				if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
			});
			
			if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
			{
				GuiUtils.showNode(piProgress, false);
				GuiUtils.showNode(lblProgress, false);
				GuiUtils.showNode(paneError, false);
				GuiUtils.showNode(paneDevicesRunnerNotRunning, true);
				GuiUtils.showNode(btnStartOver, true);
				deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
			}
			else
			{
				Map<String, Object> uiDataMap = new HashMap<>();
				uiDataMap.put(KEY_DEVICES_RUNNER_IS_RUNNING, Boolean.TRUE);
				if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
			}
		}
		else
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(lblProgress, false);
			
			if(uiInputData.get(KEY_DEVICES_RUNNER_IS_RUNNING) == Boolean.FALSE) return;
			
			@SuppressWarnings("unchecked") TaskResponse<List<Finger>> taskResponse =
									(TaskResponse<List<Finger>>) uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			if(taskResponse.isSuccess()) goNext();
			else
			{
				GuiUtils.showNode(paneError, true);
				GuiUtils.showNode(btnRetry, true);
				GuiUtils.showNode(btnStartOver, true);
				
				reportNegativeTaskResponse(taskResponse.getErrorCode(), taskResponse.getException(),
				                           taskResponse.getErrorDetails());
			}
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		GuiUtils.showNode(paneError, false);
		GuiUtils.showNode(btnRetry, false);
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(lblProgress, true);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(KEY_RETRY_PERSON_INFO_FETCHING, Boolean.TRUE);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
}