package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.commons.TaskResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RecordIdPaneFxController extends WizardStepFxControllerBase
{
	@Output private Long recordId;
	@Output private DeadPersonRecord deadPersonRecord;
	
	@FXML private TextField txtRecordId;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/recordId.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtRecordId, "\\d*", "[^\\d]", 15);
		
		btnNext.disableProperty().bind(txtRecordId.textProperty().isEmpty().or(txtRecordId.disabledProperty()));
		btnNext.setOnAction(actionEvent ->
		{
		    hideNotification();
			txtRecordId.setDisable(true);
			piProgress.setVisible(true);
			
			recordId = Long.parseLong(txtRecordId.getText());
			if(!isDetached()) Context.getWorkflowManager().submitUserTask(new HashMap<>());
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			if(recordId != null) txtRecordId.setText(String.valueOf(recordId));
			txtRecordId.requestFocus();
		}
		else
		{
			piProgress.setVisible(false);
			txtRecordId.setDisable(false);
			
			@SuppressWarnings("unchecked") TaskResponse<DeadPersonRecord> taskResponse =
								(TaskResponse<DeadPersonRecord>) uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			
			if(taskResponse.isSuccess())
			{
				deadPersonRecord = taskResponse.getResult();
				goNext();
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