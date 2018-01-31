package sa.gov.nic.bio.bw.client.features.cancelcriminal;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Map;

public class CancelCriminalPaneFxController extends BodyFxControllerBase
{
	@FXML private TextField txtPersonId;
	@FXML private TextField txtCriminalId;
	@FXML private Button btnCancelCriminal;
	@FXML private ProgressIndicator piCancelCriminal;
	
	@FXML
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtCriminalId, "\\d*", "[^\\d]", 10);
		
		BooleanBinding idNumberEmptyBinding = txtPersonId.textProperty().isEmpty();
		BooleanBinding latentNumberEmptyBinding = txtCriminalId.textProperty().isEmpty();
		BooleanProperty progressVisibility = piCancelCriminal.visibleProperty();
		
		btnCancelCriminal.disableProperty().bind(idNumberEmptyBinding.or(latentNumberEmptyBinding).or(progressVisibility));
		GuiUtils.makeButtonClickable(btnCancelCriminal);
	}
	
	@Override
	public void onControllerReady()
	{
		txtPersonId.requestFocus();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> dataMap)
	{
		if(!newForm)
		{
			ServiceResponse<?> serviceResponse = (ServiceResponse<?>) dataMap.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			disableUiControls(false);
			
			if(serviceResponse.isSuccess())
			{
				String idNumber = txtPersonId.getText();
				String latentNumber = txtCriminalId.getText();
				
				Boolean resultBean = (Boolean) serviceResponse.getResult();
				if(resultBean != null && resultBean)
				{
					String message = String.format(messagesBundle.getString("cancelCriminal.success"), latentNumber, idNumber);
					showSuccessNotification(message);
				}
				else
				{
					String message = String.format(messagesBundle.getString("cancelCriminal.failure"), latentNumber, idNumber);
					showWarningNotification(message);
				}
			}
			else handleNegativeResponse(serviceResponse);
			
			txtPersonId.requestFocus();
		}
	}
	
	@FXML
	private void onEnterPressed(ActionEvent event)
	{
		btnCancelCriminal.fire();
	}
	
	@FXML
	private void onCancelCriminalButtonClicked(ActionEvent actionEvent)
	{
		String personId = txtPersonId.getText().trim();
		String criminalId = txtCriminalId.getText().trim();
		
		String headerText = messagesBundle.getString("cancelCriminal.confirmation.header");
		String contentText = String.format(messagesBundle.getString("cancelCriminal.confirmation.message"), criminalId, personId);
		boolean confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
		
		if(!confirmed) return;
		
		hideNotification();
		disableUiControls(true);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("personId", personId);
		uiDataMap.put("criminalId", criminalId);
		
		coreFxController.submitForm(uiDataMap);
	}
	
	private void disableUiControls(boolean bool)
	{
		coreFxController.getMenuPaneController().showOverlayPane(bool);
		
		txtPersonId.setDisable(bool);
		txtCriminalId.setDisable(bool);
		
		GuiUtils.showNode(piCancelCriminal, bool);
		GuiUtils.showNode(btnCancelCriminal, !bool);
	}
}