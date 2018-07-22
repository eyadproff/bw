package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RecordIdPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_RECORD_ID = "RECORD_ID";
	
	@FXML private TextField txtRecordId;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/recordId.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtRecordId, "\\d*", "[^\\d]", 10);
		
		btnNext.disableProperty().bind(txtRecordId.textProperty().isEmpty().or(txtRecordId.disabledProperty()));
		btnNext.setOnAction(actionEvent ->
		{
		    hideNotification();
			txtRecordId.setDisable(true);
			piProgress.setVisible(true);
		
		    Map<String, Object> uiDataMap = new HashMap<>();
		    uiDataMap.put(KEY_RECORD_ID, Long.parseLong(txtRecordId.getText()));
		    Context.getWorkflowManager().submitUserTask(uiDataMap);
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			// load the old state, if exists
			Long personId = (Long) uiInputData.get(KEY_RECORD_ID);
			if(personId != null) txtRecordId.setText(String.valueOf(personId));
			
			txtRecordId.requestFocus();
		}
		else
		{
			piProgress.setVisible(false);
			txtRecordId.setDisable(false);
			
			@SuppressWarnings("unchecked")
			ServiceResponse<DeadPersonRecord> serviceResponse =
								(ServiceResponse<DeadPersonRecord>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			if(serviceResponse.isSuccess()) goNext();
			else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                        serviceResponse.getErrorDetails());
		}
	}
	
	@FXML
	private void onEnterPressed(ActionEvent actionEvent)
	{
		btnNext.fire();
	}
}