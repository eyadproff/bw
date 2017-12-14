package sa.gov.nic.bio.bw.client.login;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.client.core.*;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.interfaces.*;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
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
	private static final String FXML_CHANGE_PASSWORD = "sa/gov/nic/bio/bw/client/login/fxml/change_password_dialog.fxml";
	private static final Logger LOGGER = Logger.getLogger(LoginPaneFxController.class.getName());
	
	@FXML private TextField txtUsername;
	@FXML private PasswordField txtPassword;
	@FXML private ComboBox<GuiLanguage> cbLanguage;
	@FXML private Button btnLogin;
	@FXML private Button btnChangePassword;
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
		cbLanguage.valueProperty().addListener((observable, oldValue, newValue) ->
		{
			Locale.setDefault(newValue.getLocale());
			coreFxController.switchLanguage(newValue, this);
		});
		
		coreFxController.getMenuPaneController().hideRootPane();
		coreFxController.getHeaderPaneController().hideRootPane();
		coreFxController.getFooterPaneController().showRootPane();
		
		btnLogin.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if(event.getCode() == KeyCode.ENTER)
			{
				btnLogin.fire();
				event.consume();
			}
		});
		
		btnChangePassword.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if(event.getCode() == KeyCode.ENTER)
			{
				btnChangePassword.fire();
				event.consume();
			}
		});
		
		// request focus once the scene is attached to txtUsername
		txtUsername.sceneProperty().addListener((observable, oldValue, newValue) -> txtUsername.requestFocus());
	}
	
	@Override
	public void onReturnFromTask()
	{
		super.onReturnFromTask();
		
		txtPassword.clear();
		disableUiControls(false);
	}
	
	@FXML
	private void onLoginButtonClicked(ActionEvent event)
	{
		notificationPane.hide();
		disableUiControls(true);
		
		String username = txtUsername.getText().trim();
		String password = txtPassword.getText().trim();
		
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("username", username);
		uiDataMap.put("password", password);
		
		coreFxController.submitFormTask(uiDataMap);
	}
	
	@FXML
	private void onChangePasswordButtonClicked(ActionEvent event)
	{
		notificationPane.hide();
		
		boolean rtl = coreFxController.getGuiState().getLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		ChangePasswordDialogFxController controller = DialogUtils.buildCustomDialog(appIcon, FXML_CHANGE_PASSWORD, labelsBundle, rtl);
		
		if(controller != null)
		{
			controller.attachCoreFxController(coreFxController);
			controller.attachInitialResources(labelsBundle, errorsBundle, messagesBundle, appIcon);
			controller.setUsernameAndPassword(txtUsername.getText(), txtPassword.getText());
			controller.requestFocus();
			controller.showDialogAndWait();
			boolean passwordChanged = controller.isPasswordChangedSuccessfully();
			
			if(passwordChanged)
			{
				txtPassword.setText("");
				showSuccessNotification(messagesBundle.getString("changePassword.success"));
			}
			
			if(txtUsername.getText().isEmpty()) txtUsername.requestFocus();
			else txtPassword.requestFocus();
		}
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
		
		btnChangePassword.setManaged(!bool);
		btnChangePassword.setVisible(!bool);
	}
	
	@FXML
	private void onTextFieldEnterPressed(ActionEvent event)
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