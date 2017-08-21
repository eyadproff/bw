package sa.gov.nic.bio.bw.client.login;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sa.gov.nic.bio.bw.client.core.*;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.interfaces.*;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Fouad on 16-Jul-17.
 */
public class LoginPaneFxController extends BodyFxControllerBase implements LanguageSwitchingController
{
	private static final Logger LOGGER = Logger.getLogger(LoginPaneFxController.class.getName());
	@FXML private TextField txtUsername;
	@FXML private PasswordField txtPassword;
	@FXML private ComboBox<GuiLanguage> cbLanguage;
	@FXML private Button btnLogin;
	//@FXML private Button btnChangePassword;
	@FXML private ProgressIndicator piLogin;
	
	@FXML
	private void initialize()
	{
		cbLanguage.getItems().setAll(GuiLanguage.values());
		
		BooleanBinding usernameEmptyBinding = txtUsername.textProperty().isEmpty();
		BooleanBinding passwordEmptyBinding = txtPassword.textProperty().isEmpty();
		
		btnLogin.disableProperty().bind(usernameEmptyBinding.or(passwordEmptyBinding));
	}
	
	@Override
	public void onControllerReady()
	{
		GuiLanguage currentLanguage = coreFxController.getGuiState().getLanguage();
		cbLanguage.getSelectionModel().select(currentLanguage);
		cbLanguage.valueProperty().addListener(this::onSwitchingLanguage);
		
		coreFxController.getMenuPaneController().hideRootPane();
		coreFxController.getHeaderPaneController().hideRootPane();
		coreFxController.getFooterPaneController().showRootPane();
		
		// request focus once the scene is attached to txtUsername
		txtUsername.sceneProperty().addListener((observable, oldValue, newValue) -> txtUsername.requestFocus());
	}
	
	@Override
	public void onReturnFromTask()
	{
		txtPassword.clear();
		disableUiControls(false);
		
		String errorCode = (String) inputData.get("errorCode");
		Exception exception = (Exception) inputData.get("exception");
		String apiUrl = (String) inputData.get("apiUrl");
		Integer httpCode = (Integer) inputData.get("httpCode");
		
		if(errorCode != null)
		{
			if(errorCode.startsWith("C"))
			{
				String errorMessage = errorsBundle.getString(errorCode);
				coreFxController.showErrorDialogAndWait(errorMessage, exception);
				LOGGER.severe(errorsBundle.getString(errorCode + ".internal"));
			}
			else if(errorCode.startsWith("B"))
			{
				String errorMessage = errorsBundle.getString(errorCode);
				showWarningNotification(errorMessage);
			}
			else // server error
			{
				String code = "S000-00000";
				String guiErrorMessage = coreFxController.getErrorsBundle().getString(code);
				String logErrorMessage = coreFxController.getErrorsBundle().getString(code + ".internal");
				
				guiErrorMessage = String.format(guiErrorMessage, errorCode);
				logErrorMessage = String.format(logErrorMessage, errorCode);

				showWarningNotification(guiErrorMessage);
				LOGGER.severe(logErrorMessage);
			}
		}
		else // the server didn't send an error code inside [400,401,403,500] response
		{
			errorCode = "C002-00018";
			coreFxController.showErrorDialogAndWaitForCore(errorCode, exception, apiUrl, String.valueOf(httpCode));
		}
	}
	
	private void onSwitchingLanguage(ObservableValue<? extends GuiLanguage> observable, GuiLanguage oldValue, GuiLanguage newValue)
	{
		Locale.setDefault(newValue.getLocale());
		coreFxController.switchLanguage(newValue, this);
	}
	
	@FXML
	private void onLoginButtonClicked(ActionEvent event)
	{
		notificationPane.hide();
		disableUiControls(true);
		
		String username = txtUsername.getText();
		String password = txtPassword.getText();
		
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("username", username);
		uiDataMap.put("password", password);
		
		coreFxController.submitFormTask(uiDataMap);
	}
	
	private void disableUiControls(boolean bool)
	{
		txtUsername.setDisable(bool);
		txtPassword.setDisable(bool);
		cbLanguage.setDisable(bool);
		
		piLogin.setVisible(bool);
		piLogin.setManaged(bool);
		
		btnLogin.setManaged(!bool);
		btnLogin.setVisible(!bool);
		
		//btnChangePassword.setManaged(!bool);
		//btnChangePassword.setVisible(!bool);
	}
	
	@FXML
	private void onEnterPressed(ActionEvent event)
	{
		btnLogin.fire();
	}
	
	@Override
	public void onSaveState(StateBundle stateBundle)
	{
		String username = txtUsername.getText();
		String password = txtPassword.getText();
		
		stateBundle.putData("username", username);
		stateBundle.putData("password", password);
	}
	
	@Override
	public void onLoadState(StateBundle stateBundle)
	{
		String username = stateBundle.getDate("username", String.class);
		String password = stateBundle.getDate("password", String.class);
		
		txtUsername.setText(username);
		txtPassword.setText(password);
		cbLanguage.requestFocus();
	}
}