package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchingPersonInfoPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_DEVICES_RUNNER_IS_RUNNING = "DEVICES_RUNNER_IS_RUNNING";
	public static final String KEY_SKIP_FETCHING_PERSON_INFO = "SKIP_FETCHING_PERSON_INFO";
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
		return getClass().getResource("fxml/fetchingPersonInfo.fxml");
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
			DeadPersonRecord deadPersonRecord =
								(DeadPersonRecord) uiInputData.get(ShowRecordPaneFxController.KEY_DEAD_PERSON_RECORD);
			Long samisId = deadPersonRecord.getSamisId();
			
			if(samisId == null) goNext();
			else
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
					Context.getWorkflowManager().submitUserTask(uiDataMap);
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
					Context.getWorkflowManager().submitUserTask(uiDataMap);
				}
			}
		}
		else
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(lblProgress, false);
			
			if(uiInputData.get(KEY_DEVICES_RUNNER_IS_RUNNING) == Boolean.FALSE) return;
			
			@SuppressWarnings("unchecked") ServiceResponse<List<Finger>> serviceResponse =
									(ServiceResponse<List<Finger>>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			if(serviceResponse.isSuccess()) goNext();
			else
			{
				GuiUtils.showNode(paneError, true);
				GuiUtils.showNode(btnRetry, true);
				GuiUtils.showNode(btnStartOver, true);
				
				reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                       serviceResponse.getErrorDetails());
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
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		uiDataMap.put(KEY_SKIP_FETCHING_PERSON_INFO, Boolean.TRUE);
	}
}