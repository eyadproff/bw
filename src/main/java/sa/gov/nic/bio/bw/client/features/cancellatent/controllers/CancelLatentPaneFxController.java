package sa.gov.nic.bio.bw.client.features.cancellatent.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.HashMap;
import java.util.Map;

@FxmlFile("cancelLatent.fxml")
public class CancelLatentPaneFxController extends BodyFxControllerBase
{
	@FXML private TextField txtPersonId;
	@FXML private TextField txtLatentId;
	@FXML private Button btnCancelLatent;
	@FXML private ProgressIndicator piCancelLatent;
	
	@Override
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtLatentId, "\\d*", "[^\\d]", 20);
		
		BooleanBinding idNumberEmptyBinding = txtPersonId.textProperty().isEmpty();
		BooleanBinding latentNumberEmptyBinding = txtLatentId.textProperty().isEmpty();
		BooleanProperty progressVisibility = piCancelLatent.visibleProperty();
		
		btnCancelLatent.disableProperty().bind(idNumberEmptyBinding.or(latentNumberEmptyBinding)
				                                                   .or(progressVisibility));
	}
	
	@Override
	protected void onAttachedToScene()
	{
		txtPersonId.requestFocus();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(!newForm)
		{
			@SuppressWarnings("unchecked") TaskResponse<Boolean> taskResponse = (TaskResponse<Boolean>)
																	uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			
			disableUiControls(false);
			
			if(taskResponse.isSuccess())
			{
				String personId = txtPersonId.getText();
				String latentId = txtLatentId.getText();
				
				Boolean resultBean = taskResponse.getResult();
				if(resultBean != null && resultBean)
				{
					String message = String.format(resources.getString("cancelLatent.success"),
					                               latentId, personId);
					showSuccessNotification(message);
				}
				else
				{
					String message = String.format(resources.getString("cancelLatent.failure"),
					                               latentId, personId);
					showWarningNotification(message);
				}
			}
			else reportNegativeTaskResponse(taskResponse.getErrorCode(), taskResponse.getException(),
			                                taskResponse.getErrorDetails());
			
			txtPersonId.requestFocus();
		}
	}
	
	@FXML
	private void onEnterPressed(ActionEvent event)
	{
		btnCancelLatent.fire();
	}
	
	@FXML
	private void onCancelLatentButtonClicked(ActionEvent actionEvent)
	{
		String personId = txtPersonId.getText().trim();
		String latentId = txtLatentId.getText().trim();
		
		String headerText = resources.getString("cancelLatent.confirmation.header");
		String contentText = String.format(resources.getString("cancelLatent.confirmation.message"),
		                                   latentId, personId);
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(!confirmed) return;
		
		hideNotification();
		disableUiControls(true);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("personId", Long.parseLong(personId));
		uiDataMap.put("latentId", latentId);
		
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	private void disableUiControls(boolean bool)
	{
		txtPersonId.setDisable(bool);
		txtLatentId.setDisable(bool);
		
		GuiUtils.showNode(piCancelLatent, bool);
		GuiUtils.showNode(btnCancelLatent, !bool);
	}
}