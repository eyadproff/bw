package sa.gov.nic.bio.bw.client.cancelcriminal;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;

import java.util.HashMap;
import java.util.Map;

public class CancelCriminalPaneFxController extends BodyFxControllerBase
{
	@FXML private TextField txtPersonId;
	@FXML private TextField txtCriminalId;
	@FXML private Button btnCancelCriminal;
	@FXML private ProgressIndicator piCancelCriminal;
	
	@FXML
	private void initialize()
	{
		txtPersonId.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue.length() > 10) txtPersonId.setText(oldValue);
			
			if(!newValue.matches("\\d*"))
			{
				txtPersonId.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});
		
		txtCriminalId.textProperty().addListener((observable, oldValue, newValue) ->
        {
	        if(newValue.length() > 10) txtCriminalId.setText(oldValue);
	        
            if(!newValue.matches("\\d*"))
            {
	            txtCriminalId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
		
		BooleanBinding idNumberEmptyBinding = txtPersonId.textProperty().isEmpty();
		BooleanBinding latentNumberEmptyBinding = txtCriminalId.textProperty().isEmpty();
		BooleanProperty progressVisibility = piCancelCriminal.visibleProperty();
		
		btnCancelCriminal.disableProperty().bind(idNumberEmptyBinding.or(
											latentNumberEmptyBinding).or(progressVisibility));
	}
	
	@Override
	public void onControllerReady()
	{
		// request focus once the scene is attached to txtPersonId
		txtPersonId.sceneProperty().addListener((observable, oldValue, newValue) -> txtPersonId.requestFocus());
	}
	
	@Override
	public void onReturnFromTask()
	{
		disableUiControls(false);
		
		Boolean successResponse = (Boolean) inputData.get("successResponse");
		if(successResponse != null && successResponse)
		{
			String idNumber = txtPersonId.getText();
			String latentNumber = txtCriminalId.getText();
			
			Boolean resultBean = (Boolean) inputData.get("resultBean");
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
		else super.onReturnFromTask();
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
		
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("personId", personId);
		uiDataMap.put("criminalId", criminalId);
		
		coreFxController.submitFormTask(uiDataMap);
	}
	
	private void disableUiControls(boolean bool)
	{
		coreFxController.getMenuPaneController().showOverlayPane(bool);
		
		txtPersonId.setDisable(bool);
		txtCriminalId.setDisable(bool);
		
		piCancelCriminal.setVisible(bool);
		piCancelCriminal.setManaged(bool);
		
		btnCancelCriminal.setManaged(!bool);
		btnCancelCriminal.setVisible(!bool);
	}
}