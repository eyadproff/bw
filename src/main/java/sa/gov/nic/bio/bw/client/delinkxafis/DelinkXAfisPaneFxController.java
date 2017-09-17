package sa.gov.nic.bio.bw.client.delinkxafis;

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

public class DelinkXAfisPaneFxController extends BodyFxControllerBase
{
	@FXML private TextField txtPersonId;
	@FXML private TextField txtCriminalId;
	@FXML private Button btnDelinkXAfis;
	@FXML private ProgressIndicator piDelinkXAfis;
	
	@FXML
	private void initialize()
	{
		txtPersonId.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if(!newValue.matches("\\d*"))
			{
				txtPersonId.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});
		
		txtCriminalId.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue.matches("\\d*"))
            {
	            txtCriminalId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
		
		BooleanBinding idNumberEmptyBinding = txtPersonId.textProperty().isEmpty();
		BooleanBinding latentNumberEmptyBinding = txtCriminalId.textProperty().isEmpty();
		BooleanProperty progressVisibility = piDelinkXAfis.visibleProperty();
		
		btnDelinkXAfis.disableProperty().bind(idNumberEmptyBinding.or(
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
				String message = String.format(messagesBundle.getString("delinkXAfis.success"), latentNumber, idNumber);
				showSuccessNotification(message);
			}
			else
			{
				String message = String.format(messagesBundle.getString("delinkXAfis.failure"), latentNumber, idNumber);
				showWarningNotification(message);
			}
		}
		else super.onReturnFromTask();
	}
	
	@FXML
	private void onEnterPressed(ActionEvent event)
	{
		btnDelinkXAfis.fire();
	}
	
	public void onDelinkXAfisButtonClicked(ActionEvent actionEvent)
	{
		String personId = txtPersonId.getText().trim();
		String criminalId = txtCriminalId.getText().trim();
		
		String headerText = messagesBundle.getString("delinkXAfis.confirmation.header");
		String contentText = String.format(messagesBundle.getString("delinkXAfis.confirmation.message"), criminalId, personId);
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
		txtPersonId.setDisable(bool);
		txtCriminalId.setDisable(bool);
		
		piDelinkXAfis.setVisible(bool);
		piDelinkXAfis.setManaged(bool);
		
		btnDelinkXAfis.setManaged(!bool);
		btnDelinkXAfis.setVisible(!bool);
	}
}