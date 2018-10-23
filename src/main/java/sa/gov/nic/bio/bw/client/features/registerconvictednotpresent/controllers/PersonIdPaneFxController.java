package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.utils.RegisterConvictedNotPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.commons.TaskResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PersonIdPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_PERSON_INFO_INQUIRY_PERSON_ID = "PERSON_INFO_INQUIRY_PERSON_ID";
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtPersonId;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/personId.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		
		btnNext.disableProperty().bind(txtPersonId.textProperty().isEmpty().or(txtPersonId.disabledProperty()));
		btnNext.setOnAction(actionEvent ->
		{
			hideNotification();
			piProgress.setVisible(true);
			txtPersonId.setDisable(true);
			
			Map<String, Object> uiDataMap = new HashMap<>();
			uiDataMap.put(KEY_PERSON_INFO_INQUIRY_PERSON_ID, Long.parseLong(txtPersonId.getText()));
			uiDataMap.put(InquiryResultPaneFxController.KEY_INQUIRY_SAMIS_ID,
						  Long.parseLong(txtPersonId.getText()));
			if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm) txtPersonId.requestFocus();
		else
		{
			piProgress.setVisible(false);
			txtPersonId.setDisable(false);
			
			@SuppressWarnings("unchecked") TaskResponse<PersonInfo> taskResponse = (TaskResponse<PersonInfo>)
																	uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			
			if(taskResponse.isSuccess())
			{
				PersonInfo result = taskResponse.getResult();
				
				if(result != null)
				{
					uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT, result);
					goNext();
				}
				else
				{
					String errorCode = RegisterConvictedNotPresentErrorCodes.C009_00001.getCode();
					String[] errorDetails = {"result is null!"};
					reportNegativeTaskResponse(errorCode, null, errorDetails);
				}
			}
			else reportNegativeTaskResponse(taskResponse.getErrorCode(), taskResponse.getException(),
			                                taskResponse.getErrorDetails());
		}
	}
	
	@FXML
	private void onEnterPressed(ActionEvent actionEvent)
	{
		btnNext.fire();
	}
}