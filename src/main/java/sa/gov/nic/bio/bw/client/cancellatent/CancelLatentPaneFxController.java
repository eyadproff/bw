package sa.gov.nic.bio.bw.client.cancellatent;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.NumberStringFilteredConverter;

import java.util.HashMap;
import java.util.Map;

public class CancelLatentPaneFxController extends BodyFxControllerBase
{
	@FXML private TextField txtIdNumber;
	@FXML private TextField txtLatentNumber;
	@FXML private Button btnCancelLatent;
	@FXML private ProgressIndicator piCancelLatent;
	
	@FXML
	private void initialize()
	{
		txtIdNumber.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if(!newValue.matches("\\d*"))
			{
				txtIdNumber.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});
		
		txtLatentNumber.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue.matches("\\d*"))
            {
	            txtLatentNumber.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
		
		BooleanBinding idNumberEmptyBinding = txtIdNumber.textProperty().isEmpty();
		BooleanBinding latentNumberEmptyBinding = txtLatentNumber.textProperty().isEmpty();
		BooleanProperty progressVisibility = piCancelLatent.visibleProperty();
		
		btnCancelLatent.disableProperty().bind(idNumberEmptyBinding.or(
											latentNumberEmptyBinding).or(progressVisibility));
	}
	
	@Override
	public void onControllerReady()
	{
		// request focus once the scene is attached to txtIdNumber
		txtIdNumber.sceneProperty().addListener((observable, oldValue, newValue) -> txtIdNumber.requestFocus());
	}
	
	@Override
	public void onReturnFromTask()
	{
		disableUiControls(false);
		
		Boolean successResponse = (Boolean) inputData.get("successResponse");
		if(successResponse != null && successResponse)
		{
			String idNumber = txtIdNumber.getText();
			String latentNumber = txtLatentNumber.getText();
			
			Boolean resultBean = (Boolean) inputData.get("resultBean");
			if(resultBean != null && resultBean)
			{
				String message = String.format(messagesBundle.getString("cancelLatent.success"), latentNumber, idNumber);
				showSuccessNotification(message);
			}
			else
			{
				String message = String.format(messagesBundle.getString("cancelLatent.failure"), latentNumber, idNumber);
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
	
	public void onCancelLatentButtonClicked(ActionEvent actionEvent)
	{
		String idNumber = txtIdNumber.getText();
		String latentNumber = txtLatentNumber.getText();
		
		String headerText = messagesBundle.getString("cancelLatent.confirmation.header");
		String contentText = String.format(messagesBundle.getString("cancelLatent.confirmation.message"), latentNumber, idNumber);
		boolean confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
		
		if(!confirmed) return;
		
		hideNotification();
		disableUiControls(true);
		
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("idNumber", idNumber);
		uiDataMap.put("latentNumber", latentNumber);
		
		coreFxController.submitFormTask(uiDataMap);
	}
	
	private void disableUiControls(boolean bool)
	{
		txtIdNumber.setDisable(bool);
		txtLatentNumber.setDisable(bool);
		
		piCancelLatent.setVisible(bool);
		piCancelLatent.setManaged(bool);
		
		btnCancelLatent.setManaged(!bool);
		btnCancelLatent.setVisible(!bool);
	}
}