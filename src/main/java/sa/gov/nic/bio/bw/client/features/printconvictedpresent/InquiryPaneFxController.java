package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.utils.PrintConvictedPresentErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InquiryPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_RETRY_FINGERPRINT_INQUIRY = "RETRY_FINGERPRINT_INQUIRY";
	public static final String KEY_FINGERPRINT_INQUIRY_NO_HIT = "FINGERPRINT_INQUIRY_NO_HIT";
	public static final String KEY_FINGERPRINT_INQUIRY_HIT = "FINGERPRINT_INQUIRY_HIT";
	public static final String KEY_FINGERPRINT_INQUIRY_HIT_RESULT = "FINGERPRINT_INQUIRY_HIT_RESULT";
	
	@FXML private ProgressIndicator piProgress;
	@FXML private Label txtProgress;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterUnknownPerson;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/inquiry.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(actionEvent -> startOver());
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(!newForm)
		{
			Boolean hit = (Boolean) uiInputData.get(KEY_FINGERPRINT_INQUIRY_HIT);
			if(hit != null && hit)
			{
				Platform.runLater(this::goNext);
				return;
			}
			
			Boolean noHit = (Boolean) uiInputData.get(KEY_FINGERPRINT_INQUIRY_NO_HIT);
			if(noHit != null && noHit)
			{
				txtProgress.setText("NO HIT!");
				GuiUtils.showNode(btnRetry, true);
				GuiUtils.showNode(btnStartOver, true);
				return;
			}
			
			@SuppressWarnings("unchecked")
			ServiceResponse<Integer> serviceResponse = (ServiceResponse<Integer>)
					uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			if(serviceResponse.isSuccess())
			{
				if(serviceResponse.getResult() == null)
				{
					String errorCode = PrintConvictedPresentErrorCodes.C007_00001.getCode();
					String[] errorDetails = {"TCN is null!"};
					Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
					
					GuiUtils.showNode(btnRetry, true);
					GuiUtils.showNode(btnStartOver, true);
				}
			}
			else
			{
				String errorCode = PrintConvictedPresentErrorCodes.C007_00002.getCode();
				String[] errorDetails = {"TCN is null!",
						"serviceResponse.getErrorCode() = " + serviceResponse.getErrorCode(),
						"serviceResponse.getErrorDetails() = " + Arrays.toString(serviceResponse.getErrorDetails())};
				
				Context.getCoreFxController().showErrorDialog(errorCode, serviceResponse.getException(),
				                                              errorDetails);
			}
			
			//Platform.runLater(this::goNext);
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(btnRetry, false);
		GuiUtils.showNode(btnStartOver, false);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(KEY_RETRY_FINGERPRINT_INQUIRY, Boolean.TRUE);
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	@FXML
	private void onRegisterUnknownPersonButtonClicked(ActionEvent actionEvent)
	{
		// TODO
	}
}