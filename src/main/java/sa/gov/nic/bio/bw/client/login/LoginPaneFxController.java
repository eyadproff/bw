package sa.gov.nic.bio.bw.client.login;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.interfaces.PersistableEntity;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginPaneFxController extends BodyFxControllerBase implements PersistableEntity
{
	private static final String FXML_CHANGE_PASSWORD =
													"sa/gov/nic/bio/bw/client/login/fxml/change_password_dialog.fxml";
	
	@FXML private TextField txtUsername;
	@FXML private PasswordField txtPassword;
	@FXML private ComboBox<GuiLanguage> cboLanguage;
	@FXML private Button btnLogin;
	@FXML private Button btnChangePassword;
	@FXML private ProgressIndicator piLogin;
	
	@Override
	protected void initialize()
	{
		cboLanguage.getItems().setAll(GuiLanguage.values());
		GuiUtils.applyValidatorToTextField(txtUsername, 256);
		GuiUtils.applyValidatorToTextField(txtPassword, 256);
		
		BooleanBinding usernameEmptyBinding = txtUsername.textProperty().isEmpty();
		BooleanBinding passwordEmptyBinding = txtPassword.textProperty().isEmpty();
		
		btnLogin.disableProperty().bind(usernameEmptyBinding.or(passwordEmptyBinding));
	}
	
	@Override
	protected void onAttachedToScene()
	{
		cboLanguage.getSelectionModel().select(Context.getGuiLanguage());
		cboLanguage.setOnAction(event ->
        {
	        GuiLanguage guiLanguage = cboLanguage.getValue();
	        Locale.setDefault(guiLanguage.getLocale());
	        Context.getCoreFxController().switchLanguage(guiLanguage, this);
        });

		Context.getCoreFxController().getHeaderPaneController().hideRegion();
		Context.getCoreFxController().getMenuPaneController().hideRegion();
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().hideRegion();
		Context.getCoreFxController().getFooterPaneController().showRegion();
		
		GuiUtils.makeButtonClickableByPressingEnter(btnLogin);
		GuiUtils.makeButtonClickableByPressingEnter(btnChangePassword);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboLanguage);
		
		// request focus once the scene is attached to txtUsername
		txtUsername.sceneProperty().addListener((observable, oldValue, newValue) -> txtUsername.requestFocus());
		
		if(Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL ||
		   Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
		{
			txtUsername.setText(Context.getConfigManager().getProperty("dev.login.username"));
			txtPassword.setText(Context.getConfigManager().getProperty("dev.login.password"));
		}
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(!newForm)
		{
			txtPassword.clear();
			disableUiControls(false);
			
			ServiceResponse<?> serviceResponse = (ServiceResponse<?>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			if(!serviceResponse.isSuccess())
			{
				reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                       serviceResponse.getErrorDetails());
			}
		}
	}
	
	@FXML
	private void onLoginButtonClicked(ActionEvent event)
	{
		hideNotification();
		disableUiControls(true);
		
		String username = txtUsername.getText().trim();
		String password = txtPassword.getText().trim();
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("username", username);
		uiDataMap.put("password", password);
		
		Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	@FXML
	private void onChangePasswordButtonClicked(ActionEvent event)
	{
		hideNotification();
		
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		ChangePasswordDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
				Context.getCoreFxController().getStage(), FXML_CHANGE_PASSWORD, resources, rtl);
		
		if(controller != null)
		{
			controller.setUsernameAndPassword(txtUsername.getText(), txtPassword.getText());
			controller.requestFocus();
			controller.showDialogAndWait();
			boolean passwordChanged = controller.isPasswordChangedSuccessfully();
			
			if(passwordChanged)
			{
				txtPassword.setText("");
				showSuccessNotification(resources.getString("changePassword.success"));
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
		//GuiUtils.showNode(btnChangePassword, !bool);
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