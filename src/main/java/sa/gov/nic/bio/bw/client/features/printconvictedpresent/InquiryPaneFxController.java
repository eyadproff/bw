package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.utils.PrintConvictedPresentErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class InquiryPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_WAITING_FINGERPRINT_INQUIRY = "WAITING_FINGERPRINT_INQUIRY";
	public static final String KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED = "WAITING_FINGERPRINT_INQUIRY_CANCELLED";
	public static final String KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS = "FINGERPRINT_INQUIRY_UNKNOWN_STATUS";
	public static final String KEY_RETRY_FINGERPRINT_INQUIRY = "RETRY_FINGERPRINT_INQUIRY";
	public static final String KEY_FINGERPRINT_INQUIRY_HIT = "FINGERPRINT_INQUIRY_HIT";
	public static final String KEY_FINGERPRINT_INQUIRY_HIT_RESULT = "FINGERPRINT_INQUIRY_HIT_RESULT";
	
	@FXML private VBox paneError;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label txtProgress;
	@FXML private Label lblCancelled;
	@FXML private Button btnCancel;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	private boolean fingerprintInquiryCancelled = false;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/inquiry.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnRetry);
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		
		btnStartOver.setOnAction(actionEvent ->
		{
			hideNotification();
			startOver();
		});
		btnCancel.setOnAction(actionEvent ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(txtProgress, false);
			GuiUtils.showNode(btnCancel, false);
			GuiUtils.showNode(btnRetry, true);
			GuiUtils.showNode(btnStartOver, true);
			fingerprintInquiryCancelled = true;
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(!newForm)
		{
			Boolean hit = (Boolean) uiInputData.get(KEY_FINGERPRINT_INQUIRY_HIT);
			if(hit != null)
			{
				Platform.runLater(this::goNext);
				return;
			}
			
			Integer unknownStatus = (Integer) uiInputData.get(KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS);
			if(unknownStatus != null)
			{
				String errorCode = PrintConvictedPresentErrorCodes.C007_00001.getCode();
				String[] errorDetails = {"Unknown fingerprint inquiry status (" + unknownStatus + ")!"};
				Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
				return;
			}
			
			Boolean waiting = (Boolean) uiInputData.get(InquiryPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY);
			if(waiting != null && waiting)
			{
				GuiUtils.showNode(btnCancel, true);
				Context.getExecutorService().submit(() ->
				{
					int seconds = Integer.parseInt(System.getProperty(
										"jnlp.bio.bw.printConvictedReport.fingerprint.inquiry.checkEvertSeconds"));
					
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
							uiDataMap.put(KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED, Boolean.TRUE);
							GuiUtils.showNode(lblCancelled, true);
						}
						Context.getWorkflowManager().submitUserTask(uiDataMap);
					});
				});
				return;
			}
			
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(txtProgress, false);
			GuiUtils.showNode(btnCancel, false);
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
					String errorCode = PrintConvictedPresentErrorCodes.C007_00002.getCode();
					String[] errorDetails = {"TCN is null!"};
					Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
				}
			}
			else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                        serviceResponse.getErrorDetails());
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		GuiUtils.showNode(btnRetry, false);
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(paneError, false);
		GuiUtils.showNode(lblCancelled, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(txtProgress, true);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(KEY_RETRY_FINGERPRINT_INQUIRY, Boolean.TRUE);
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
}