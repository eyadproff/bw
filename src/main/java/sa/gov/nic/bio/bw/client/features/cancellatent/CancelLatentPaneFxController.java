package sa.gov.nic.bio.bw.client.features.cancellatent;

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

public class CancelLatentPaneFxController extends BodyFxControllerBase
{
	@FXML private TextField txtPersonId;
	@FXML private TextField txtLatentId;
	@FXML private Button btnCancelLatent;
	@FXML private ProgressIndicator piCancelLatent;
	
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
		
		txtLatentId.textProperty().addListener((observable, oldValue, newValue) ->
        {
	        if(newValue.length() > 20) txtLatentId.setText(oldValue);
        	
            if(!newValue.matches("\\d*"))
            {
	            txtLatentId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
		
		BooleanBinding idNumberEmptyBinding = txtPersonId.textProperty().isEmpty();
		BooleanBinding latentNumberEmptyBinding = txtLatentId.textProperty().isEmpty();
		BooleanProperty progressVisibility = piCancelLatent.visibleProperty();
		
		btnCancelLatent.disableProperty().bind(idNumberEmptyBinding.or(
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
			String personId = txtPersonId.getText();
			String latentId = txtLatentId.getText();
			
			Boolean resultBean = (Boolean) inputData.get("resultBean");
			if(resultBean != null && resultBean)
			{
				String message = String.format(messagesBundle.getString("cancelLatent.success"), latentId, personId);
				showSuccessNotification(message);
			}
			else
			{
				String message = String.format(messagesBundle.getString("cancelLatent.failure"), latentId, personId);
				showWarningNotification(message);
			}
		}
		else super.onReturnFromTask();
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
		
		String headerText = messagesBundle.getString("cancelLatent.confirmation.header");
		String contentText = String.format(messagesBundle.getString("cancelLatent.confirmation.message"), latentId, personId);
		boolean confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
		
		if(!confirmed) return;
		
		hideNotification();
		disableUiControls(true);
		
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("personId", personId);
		uiDataMap.put("latentId", latentId);
		
		coreFxController.submitFormTask(uiDataMap);
	}
	
	private void disableUiControls(boolean bool)
	{
		coreFxController.getMenuPaneController().showOverlayPane(bool);
		
		txtPersonId.setDisable(bool);
		txtLatentId.setDisable(bool);
		
		piCancelLatent.setVisible(bool);
		piCancelLatent.setManaged(bool);
		
		btnCancelLatent.setManaged(!bool);
		btnCancelLatent.setVisible(!bool);
	}
}