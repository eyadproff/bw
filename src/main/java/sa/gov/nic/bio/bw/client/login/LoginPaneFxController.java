package sa.gov.nic.bio.bw.client.login;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.interfaces.PersistableEntity;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginPaneFxController extends BodyFxControllerBase implements PersistableEntity
{
	private static final String FXML_CHANGE_FINGERPRINT =
												"sa/gov/nic/bio/bw/client/login/fxml/change_fingerprint_dialog.fxml";
	private static final String FXML_CHANGE_PASSWORD =
												"sa/gov/nic/bio/bw/client/login/fxml/change_password_dialog.fxml";
	
	@FXML private Label lblFingerprintScannerNotInitialized;
	@FXML private Label lblFingerprintScannerNotConnected;
	@FXML private Label lblFingerprintScannerInitialized;
	@FXML private AutoScalingStackPane spLeftHand;
	@FXML private AutoScalingStackPane spRightHand;
	@FXML private SVGPath svgLeftLittle;
	@FXML private SVGPath svgLeftRing;
	@FXML private SVGPath svgLeftMiddle;
	@FXML private SVGPath svgLeftIndex;
	@FXML private SVGPath svgLeftThumb;
	@FXML private SVGPath svgRightLittle;
	@FXML private SVGPath svgRightRing;
	@FXML private SVGPath svgRightMiddle;
	@FXML private SVGPath svgRightIndex;
	@FXML private SVGPath svgRightThumb;
	@FXML private TabPane tabPane;
	@FXML private Tab tabLoginByPassword;
	@FXML private Tab tabLoginByFingerprint;
	@FXML private TextField txtUsernameLoginByPassword;
	@FXML private TextField txtUsernameLoginByFingerprint;
	@FXML private PasswordField txtPassword;
	@FXML private ComboBox<GuiLanguage> cboLanguage;
	@FXML private Button btnFingerprintScannerAction;
	@FXML private Button btnChangeFingerprint;
	@FXML private Button btnLogin;
	@FXML private Button btnChangePassword;
	@FXML private ProgressIndicator piLogin;
	
	private FingerPosition currentFingerPosition;
	
	@Override
	protected void initialize()
	{
		tabLoginByPassword.setGraphic(AppUtils.createFontAwesomeIcon(Glyph.LOCK));
		tabLoginByFingerprint.setGraphic(AppUtils.createFontAwesomeIcon(Glyph.HAND_ALT_UP));
		
		tabLoginByPassword.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
			    txtUsernameLoginByPassword.setText(txtUsernameLoginByFingerprint.getText());
		    	Platform.runLater(txtUsernameLoginByPassword::requestFocus);
		    }
		});
		
		tabLoginByFingerprint.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				txtUsernameLoginByFingerprint.setText(txtUsernameLoginByPassword.getText());
				Platform.runLater(txtUsernameLoginByFingerprint::requestFocus);
			}
		});
		
		org.controlsfx.glyphfont.Glyph gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
		btnFingerprintScannerAction.setGraphic(gearIcon);
		
		cboLanguage.getItems().setAll(GuiLanguage.values());
		GuiUtils.applyValidatorToTextField(txtUsernameLoginByPassword, 256);
		GuiUtils.applyValidatorToTextField(txtPassword, 256);
		
		BooleanBinding usernameEmptyBinding = txtUsernameLoginByPassword.textProperty().isEmpty();
		BooleanBinding passwordEmptyBinding = txtPassword.textProperty().isEmpty();
		
		btnLogin.disableProperty().bind(usernameEmptyBinding.or(passwordEmptyBinding));
		
		currentFingerPosition = FingerPosition.RIGHT_INDEX;
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
		
		// request focus once the scene is attached to txtUsernameLoginByPassword
		txtUsernameLoginByPassword.sceneProperty().addListener((observable, oldValue, newValue) -> txtUsernameLoginByPassword.requestFocus());
		
		if(Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL ||
		   Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
		{
			txtUsernameLoginByPassword.setText(Context.getConfigManager().getProperty("dev.login.username"));
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
		
		String username = txtUsernameLoginByPassword.getText().trim();
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
			controller.setUsernameAndPassword(txtUsernameLoginByPassword.getText(), txtPassword.getText());
			controller.requestFocus();
			controller.showDialogAndWait();
			boolean passwordChanged = controller.isPasswordChangedSuccessfully();
			
			if(passwordChanged)
			{
				txtPassword.setText("");
				showSuccessNotification(resources.getString("changePassword.success"));
			}
			
			if(txtUsernameLoginByPassword.getText().isEmpty()) txtUsernameLoginByPassword.requestFocus();
			else txtPassword.requestFocus();
		}
	}
	
	private void disableUiControls(boolean bool)
	{
		txtUsernameLoginByPassword.setDisable(bool);
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
		String username = txtUsernameLoginByPassword.getText();
		String password = txtPassword.getText();
		
		stateBundle.putData("username", username);
		stateBundle.putData("password", password);
	}
	
	@Override
	public void onLoadState(StateBundle stateBundle)
	{
		String username = stateBundle.getDate("username", String.class);
		String password = stateBundle.getDate("password", String.class);
		
		txtUsernameLoginByPassword.setText(username);
		txtPassword.setText(password);
		cboLanguage.requestFocus();
	}
	
	@FXML
	private void onFingerprintScannerActionButtonClicked(MouseEvent mouseEvent)
	{
	
	}
	
	@FXML
	private void onChangeFingerprintButtonClicked(ActionEvent actionEvent)
	{
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		ChangeFingerprintDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
				Context.getCoreFxController().getStage(), FXML_CHANGE_FINGERPRINT, resources, rtl);
		
		if(controller != null)
		{
			controller.setCurrentFingerPosition(currentFingerPosition);
			boolean confirmed = controller.showDialogAndWait();
			
			if(confirmed)
			{
				FingerPosition fingerPosition = controller.getCurrentFingerPosition();
				if(fingerPosition != null) activateFingerprint(fingerPosition);
			}
		}
	}
	
	private void activateFingerprint(FingerPosition fingerPosition)
	{
		currentFingerPosition = fingerPosition;
		
		GuiUtils.showNode(svgLeftLittle, fingerPosition == FingerPosition.LEFT_LITTLE);
		GuiUtils.showNode(svgLeftRing, fingerPosition == FingerPosition.LEFT_RING);
		GuiUtils.showNode(svgLeftMiddle, fingerPosition == FingerPosition.LEFT_MIDDLE);
		GuiUtils.showNode(svgLeftIndex, fingerPosition == FingerPosition.LEFT_INDEX);
		GuiUtils.showNode(svgLeftThumb, fingerPosition == FingerPosition.LEFT_THUMB);
		GuiUtils.showNode(svgRightLittle, fingerPosition == FingerPosition.RIGHT_LITTLE);
		GuiUtils.showNode(svgRightRing, fingerPosition == FingerPosition.RIGHT_RING);
		GuiUtils.showNode(svgRightMiddle, fingerPosition == FingerPosition.RIGHT_MIDDLE);
		GuiUtils.showNode(svgRightIndex, fingerPosition == FingerPosition.RIGHT_INDEX);
		GuiUtils.showNode(svgRightThumb, fingerPosition == FingerPosition.RIGHT_THUMB);
	}
}