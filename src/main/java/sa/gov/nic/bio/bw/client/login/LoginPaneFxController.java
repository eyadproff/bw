package sa.gov.nic.bio.bw.client.login;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import sa.gov.nic.bio.bw.client.core.*;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.interfaces.*;
import sa.gov.nic.bio.bw.client.core.utils.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fouad on 16-Jul-17.
 */
public class LoginPaneFxController extends BodyFxControllerBase implements LanguageSwitchingController
{
	private static final String FXML_CHANGE_PASSWORD = "sa/gov/nic/bio/bw/client/login/fxml/change_password_dialog.fxml";
	
	@FXML private TextField txtUsername;
	@FXML private PasswordField txtPassword;
	@FXML private ComboBox<GuiLanguage> cboLanguage;
	@FXML private Button btnLogin;
	@FXML private Button btnChangePassword;
	@FXML private ProgressIndicator piLogin;
	
	@FXML
	private void initialize()
	{
		cboLanguage.getItems().setAll(GuiLanguage.values());
		GuiUtils.applyValidatorToTextField(txtUsername, 256);
		GuiUtils.applyValidatorToTextField(txtPassword, 256);
		
		BooleanBinding usernameEmptyBinding = txtUsername.textProperty().isEmpty();
		BooleanBinding passwordEmptyBinding = txtPassword.textProperty().isEmpty();
		
		btnLogin.disableProperty().bind(usernameEmptyBinding.or(passwordEmptyBinding));
	}
	
	@Override
	public void onControllerReady()
	{
		GuiLanguage currentLanguage = coreFxController.getGuiState().getLanguage();
		cboLanguage.getSelectionModel().select(currentLanguage);
		cboLanguage.setOnAction(event ->
        {
	        GuiLanguage guiLanguage = cboLanguage.getValue();
	        Locale.setDefault(guiLanguage.getLocale());
	        coreFxController.switchLanguage(guiLanguage, this);
        });
		
		coreFxController.getMenuPaneController().hideRootPane();
		coreFxController.getHeaderPaneController().hideRootPane();
		coreFxController.getFooterPaneController().showRootPane();
		
		GuiUtils.makeButtonClickable(btnLogin);
		GuiUtils.makeButtonClickable(btnChangePassword);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBarAndEnter(cboLanguage);
		
		// request focus once the scene is attached to txtUsername
		txtUsername.sceneProperty().addListener((observable, oldValue, newValue) -> txtUsername.requestFocus());
		
		if(Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
		{
			txtUsername.setText(Context.getConfigManager().getProperty("dev.login.username"));
			txtPassword.setText(Context.getConfigManager().getProperty("dev.login.password"));
		}
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
		hideNotification();
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
		hideNotification();
		
		boolean rtl = coreFxController.getGuiState().getLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		ChangePasswordDialogFxController controller = DialogUtils.buildCustomDialog(coreFxController.getPrimaryStage(), appIcon, FXML_CHANGE_PASSWORD, labelsBundle, rtl);
		
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
		cboLanguage.setDisable(bool);
		
		GuiUtils.showNode(piLogin, bool);
		GuiUtils.showNode(btnLogin, !bool);
		GuiUtils.showNode(btnChangePassword, !bool);
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
		cboLanguage.requestFocus();
	}
}