package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent;

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
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.utils.RegisterConvictedNotPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchingFingerprintsPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_RETRY_FINGERPRINT_FETCHING = "RETRY_FINGERPRINT_FETCHING";
	public static final String KEY_PERSON_FINGERPRINTS = "PERSON_FINGERPRINTS";
	public static final String KEY_PERSON_MISSING_FINGERPRINTS = "PERSON_MISSING_FINGERPRINTS";
	public static final String KEY_PERSON_FINGERPRINTS_IMAGES = "PERSON_FINGERPRINTS_IMAGES";
	
	@FXML private ProgressIndicator piProgress;
	@FXML private Label txtProgress;
	@FXML private VBox paneError;
	@FXML private Button btnStartOver;
	@FXML private Button btnRetry;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/fetchingFingerprints.fxml");
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
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(!newForm)
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(txtProgress, false);
			
			@SuppressWarnings("unchecked") ServiceResponse<List<Finger>> serviceResponse =
									(ServiceResponse<List<Finger>>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			if(serviceResponse.isSuccess())
			{
				List<Finger> result = serviceResponse.getResult();
				if(result != null)
				{
					uiInputData.put(KEY_PERSON_FINGERPRINTS, result);
					goNext();
				}
				else
				{
					GuiUtils.showNode(paneError, true);
					GuiUtils.showNode(btnRetry, true);
					GuiUtils.showNode(btnStartOver, true);
					
					String errorCode = RegisterConvictedNotPresentErrorCodes.C009_00002.getCode();
					String[] errorDetails = {"result is null!"};
					reportNegativeResponse(errorCode, null, errorDetails);
				}
			}
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
		GuiUtils.showNode(txtProgress, true);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(KEY_RETRY_FINGERPRINT_FETCHING, Boolean.TRUE);
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
}