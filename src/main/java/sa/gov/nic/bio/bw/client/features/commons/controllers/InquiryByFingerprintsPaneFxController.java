package sa.gov.nic.bio.bw.client.features.commons.controllers;

import javafx.application.Platform;
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
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.commons.TaskResponse;

import java.net.URL;
import java.util.Map;

public class InquiryByFingerprintsPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_WAITING_FINGERPRINT_INQUIRY = "WAITING_FINGERPRINT_INQUIRY";
	public static final String KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED = "WAITING_FINGERPRINT_INQUIRY_CANCELLED";
	public static final String KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS = "FINGERPRINT_INQUIRY_UNKNOWN_STATUS";
	public static final String KEY_RETRY_FINGERPRINT_INQUIRY = "RETRY_FINGERPRINT_INQUIRY";
	public static final String KEY_FINGERPRINT_INQUIRY_HIT = "FINGERPRINT_INQUIRY_HIT";
	public static final String KEY_DEVICES_RUNNER_IS_RUNNING = "DEVICES_RUNNER_IS_RUNNING";
	public static final String KEY_INQUIRY_ERROR_CODE = "INQUIRY_ERROR_CODE";
	public static final String KEY_INQUIRY_ERROR_EXCEPTION = "INQUIRY_ERROR_EXCEPTION";
	public static final String KEY_INQUIRY_ERROR_DETAILS = "INQUIRY_ERROR_DETAILS";
	
	@Input private Status status;
	
	@FXML private VBox paneError;
	@FXML private VBox paneDevicesRunnerNotRunning;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblProgress;
	@FXML private Label lblCanceling;
	@FXML private Label lblCancelled;
	@FXML private Button btnCancel;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	private boolean fingerprintInquiryCancelled = false;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/inquiryByFingerprints.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(actionEvent ->
		{
			hideNotification();
			startOver();
		});
		btnCancel.setOnAction(actionEvent ->
		{
			GuiUtils.showNode(lblProgress, false);
			GuiUtils.showNode(btnCancel, false);
			GuiUtils.showNode(lblCanceling, true);
			fingerprintInquiryCancelled = true;
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
				GuiUtils.showNode(btnCancel, running);
				GuiUtils.showNode(paneDevicesRunnerNotRunning, !running);
				GuiUtils.showNode(btnStartOver, !running);
				
				if(running && !isDetached()) continueWorkflow();
			});
			
			if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
			{
				GuiUtils.showNode(piProgress, false);
				GuiUtils.showNode(lblProgress, false);
				GuiUtils.showNode(btnCancel, false);
				GuiUtils.showNode(lblCanceling, false);
				GuiUtils.showNode(paneError, false);
				GuiUtils.showNode(paneDevicesRunnerNotRunning, true);
				GuiUtils.showNode(btnStartOver, true);
				deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
			}
			else continueWorkflow();
		}
		else
		{
			@SuppressWarnings("unchecked") TaskResponse<Integer> taskResponse = (TaskResponse<Integer>)
																uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			
			if(taskResponse.isSuccess())
			{
				if(status == null)
				{
					showControlsOnError();
					
					String errorCode = FingerprintCardIdentificationErrorCodes.C013_00011.getCode();
					String[] errorDetails = {"The fingerprint inquiry status is not set!"};
					reportNegativeTaskResponse(errorCode, null, errorDetails);
					return;
				}
				
				if(status == Status.PENDING)
				{
					btnCancel.setDisable(false);
					Context.getExecutorService().submit(() ->
					{
					    int seconds = Integer.parseInt(
					            Context.getConfigManager().getProperty("fingerprint.inquiry.checkEverySeconds"));
					
					    try
					    {
					        Thread.sleep(seconds * 1000);
					    }
					    catch(InterruptedException e)
					    {
					        e.printStackTrace();
					    }
					
					    Platform.runLater(() ->
					    {
					        if(fingerprintInquiryCancelled)
					        {
					            fingerprintInquiryCancelled = false;
					            GuiUtils.showNode(piProgress, false);
					            GuiUtils.showNode(lblCanceling, false);
					            GuiUtils.showNode(lblCancelled, true);
					            GuiUtils.showNode(btnStartOver, true);
					            GuiUtils.showNode(btnRetry, true);
					        }
					        else continueWorkflow();
					    });
					});
				}
				else goNext();
			}
			else
			{
				showControlsOnError();
				reportNegativeTaskResponse(taskResponse.getErrorCode(), taskResponse.getException(),
				                           taskResponse.getErrorDetails());
			}
		}
	}
	
	private void showControlsOnError()
	{
		GuiUtils.showNode(piProgress, false);
		GuiUtils.showNode(lblProgress, false);
		GuiUtils.showNode(btnCancel, false);
		GuiUtils.showNode(lblCanceling, false);
		GuiUtils.showNode(btnRetry, true);
		GuiUtils.showNode(btnStartOver, true);
		GuiUtils.showNode(paneError, true);
	}
	
	@Override
	protected void onDetachingFromScene()
	{
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(null);
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		GuiUtils.showNode(paneError, false);
		GuiUtils.showNode(lblCancelled, false);
		GuiUtils.showNode(btnRetry, false);
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(lblProgress, true);
		GuiUtils.showNode(btnCancel, true);
		
		continueWorkflow();
	}
}