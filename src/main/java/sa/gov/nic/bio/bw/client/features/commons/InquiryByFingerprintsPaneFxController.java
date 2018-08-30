package sa.gov.nic.bio.bw.client.features.commons;

import javafx.application.Platform;
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
import sa.gov.nic.bio.bw.client.features.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
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
		return getClass().getResource("fxml/inquiryByFingerprints.fxml");
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
				
				if(running)
				{
					Map<String, Object> uiDataMap = new HashMap<>();
					uiDataMap.put(KEY_DEVICES_RUNNER_IS_RUNNING, Boolean.TRUE);
					Context.getWorkflowManager().submitUserTask(uiDataMap);
				}
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
			else
			{
				Map<String, Object> uiDataMap = new HashMap<>();
				uiDataMap.put(KEY_DEVICES_RUNNER_IS_RUNNING, Boolean.TRUE);
				Context.getWorkflowManager().submitUserTask(uiDataMap);
			}
		}
		else
		{
			String errorCode = (String) uiInputData.get(KEY_INQUIRY_ERROR_CODE);
			if(errorCode != null)
			{
				GuiUtils.showNode(piProgress, false);
				GuiUtils.showNode(lblProgress, false);
				GuiUtils.showNode(btnCancel, false);
				GuiUtils.showNode(lblCanceling, false);
				GuiUtils.showNode(btnRetry, true);
				GuiUtils.showNode(btnStartOver, true);
				GuiUtils.showNode(paneError, true);
				
				Throwable exception = (Throwable) uiInputData.get(KEY_INQUIRY_ERROR_EXCEPTION);
				String[] errorDetails = (String[]) uiInputData.get(KEY_INQUIRY_ERROR_DETAILS);
				
				reportNegativeResponse(errorCode, exception, errorDetails);
				return;
			}
			
			Boolean hitResult = (Boolean) uiInputData.get(KEY_FINGERPRINT_INQUIRY_HIT);
			if(hitResult != null)
			{
				goNext();
				return;
			}
			
			Integer unknownStatus = (Integer) uiInputData.get(KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS);
			if(unknownStatus != null)
			{
				errorCode = CommonsErrorCodes.C008_00017.getCode();
				String[] errorDetails = {"Unknown fingerprint inquiry status (" + unknownStatus + ")!"};
				Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
				return;
			}
			
			Boolean waiting = (Boolean) uiInputData.get(
												InquiryByFingerprintsPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY);
			if(waiting != null && waiting)
			{
				btnCancel.setDisable(false);
				Context.getExecutorService().submit(() ->
				{
					int seconds = Integer.parseInt(
						System.getProperty("jnlp.bio.bw.fingerprint.inquiry.checkEverySeconds"));
					
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
						Map<String, Object> uiDataMap = new HashMap<>();
						if(fingerprintInquiryCancelled)
						{
							fingerprintInquiryCancelled = false;
							
							GuiUtils.showNode(piProgress, false);
							GuiUtils.showNode(lblCanceling, false);
							GuiUtils.showNode(lblCancelled, true);
							GuiUtils.showNode(btnStartOver, true);
							GuiUtils.showNode(btnRetry, true);
							
							uiDataMap.put(KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED, Boolean.TRUE);
						}
						Context.getWorkflowManager().submitUserTask(uiDataMap);
					});
				});
				return;
			}
			
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(lblProgress, false);
			GuiUtils.showNode(btnCancel, false);
			GuiUtils.showNode(lblCanceling, false);
			GuiUtils.showNode(btnRetry, true);
			GuiUtils.showNode(btnStartOver, true);
			GuiUtils.showNode(paneError, true);
			
			@SuppressWarnings("unchecked")
			ServiceResponse<Integer> serviceResponse = (ServiceResponse<Integer>)
																	uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			if(serviceResponse.isSuccess())
			{
				if(serviceResponse.getResult() == null)
				{
					errorCode = CommonsErrorCodes.C008_00018.getCode();
					String[] errorDetails = {"TCN is null!"};
					reportNegativeResponse(errorCode, null, errorDetails);
				}
			}
			else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                        serviceResponse.getErrorDetails());
		}
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
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(KEY_RETRY_FINGERPRINT_INQUIRY, Boolean.TRUE);
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
}