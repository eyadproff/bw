package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.controllers.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.utils.RegisterConvictedNotPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.commons.TaskResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchingFingerprintsPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_RETRY_FINGERPRINT_FETCHING = "RETRY_FINGERPRINT_FETCHING";
	
	@FXML private ProgressIndicator piProgress;
	@FXML private Label txtProgress;
	@FXML private VBox paneError;
	@FXML private Button btnStartOver;
	@FXML private Button btnRetry;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/fetchingFingerprints.fxml");
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
		if(!newForm)
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(txtProgress, false);
			
			@SuppressWarnings("unchecked") TaskResponse<List<Finger>> taskResponse =
									(TaskResponse<List<Finger>>) uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			if(taskResponse.isSuccess())
			{
				List<Finger> result = taskResponse.getResult();
				if(result != null)
				{
					uiInputData.put(FingerprintCapturingFxController.KEY_SLAP_FINGERPRINTS, result);
					goNext();
				}
				else
				{
					GuiUtils.showNode(paneError, true);
					GuiUtils.showNode(btnRetry, true);
					GuiUtils.showNode(btnStartOver, true);
					
					String errorCode = RegisterConvictedNotPresentErrorCodes.C009_00002.getCode();
					String[] errorDetails = {"result is null!"};
					reportNegativeTaskResponse(errorCode, null, errorDetails);
				}
			}
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
		GuiUtils.showNode(txtProgress, true);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(KEY_RETRY_FINGERPRINT_FETCHING, Boolean.TRUE);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
}