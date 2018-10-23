package sa.gov.nic.bio.bw.client.login.controllers;

import javafx.application.Platform;
import javafx.beans.binding.BooleanExpression;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;
import sa.gov.nic.bio.biokit.websocket.ClosureListener;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.StateBundle;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.interfaces.PersistableEntity;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.Device;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.FingerprintDeviceType;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.features.commons.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.client.login.utils.LoginErrorCodes;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

@FxmlFile("login.fxml")
public class LoginPaneFxController extends BodyFxControllerBase implements PersistableEntity
{
	public enum LoginMethod
	{
		USERNAME_AND_PASSWORD, USERNAME_AND_FINGERPRINT
	}
	
	@Output private String username;
	@Output private String password;
	@Output private Integer fingerPosition;
	@Output private String fingerprint;
	@Output private LoginMethod loginMethod;
	
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
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setNextFingerprintDeviceType(FingerprintDeviceType.SINGLE);
		
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
		{
		    boolean autoInitialize = "true".equals(
		    		                            Context.getConfigManager().getProperty("fingerprint.autoInitialize"));
		
		    if(running && autoInitialize &&
				    !deviceManagerGadgetPaneController.isFingerprintScannerInitialized(FingerprintDeviceType.SINGLE))
		    {
		        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SINGLE);
		    }
		});
		
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
				Platform.runLater(() ->
				{
					txtUsernameLoginByFingerprint.requestFocus();
					initializeFingerprintScanner();
				});
			}
		});
		
		org.controlsfx.glyphfont.Glyph gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
		btnFingerprintScannerAction.setGraphic(gearIcon);
		
		cboLanguage.getItems().setAll(GuiLanguage.values());
		GuiUtils.applyValidatorToTextField(txtUsernameLoginByPassword, 256);
		GuiUtils.applyValidatorToTextField(txtPassword, 256);
		
		BooleanExpression loginByPasswordTabBinding = tabLoginByPassword.selectedProperty();
		BooleanExpression usernameLoginByPasswordEmptyBinding = txtUsernameLoginByPassword.textProperty().isEmpty();
		BooleanExpression passwordEmptyBinding = txtPassword.textProperty().isEmpty();
		
		BooleanExpression loginByFingerprintTabBinding = tabLoginByFingerprint.selectedProperty();
		BooleanExpression usernameLoginByFingerprintEmptyBinding =
																txtUsernameLoginByFingerprint.textProperty().isEmpty();
		BooleanExpression fingerprintScannerInitializationBinding = lblFingerprintScannerInitialized.visibleProperty();
		
		btnLogin.disableProperty().bind(loginByPasswordTabBinding.and(usernameLoginByPasswordEmptyBinding
				                                                                            .or(passwordEmptyBinding))
	                                .or(loginByFingerprintTabBinding.and(usernameLoginByFingerprintEmptyBinding
                                                                 .or(fingerprintScannerInitializationBinding.not()))));
		
		spLeftHand.setOnMouseClicked(event -> btnChangeFingerprint.fire());
		spRightHand.setOnMouseClicked(event -> btnChangeFingerprint.fire());
		
		lblFingerprintScannerNotConnected.visibleProperty().bind(
						deviceManagerGadgetPaneController.getFingerprintScannerNotConnectedLabel().visibleProperty());
		lblFingerprintScannerNotConnected.managedProperty().bind(
						deviceManagerGadgetPaneController.getFingerprintScannerNotConnectedLabel().managedProperty());
		
		lblFingerprintScannerNotInitialized.visibleProperty().bind(
						deviceManagerGadgetPaneController.getFingerprintScannerNotInitializedLabel().visibleProperty());
		lblFingerprintScannerNotInitialized.managedProperty().bind(
						deviceManagerGadgetPaneController.getFingerprintScannerNotInitializedLabel().managedProperty());
		
		lblFingerprintScannerInitialized.visibleProperty().bind(
						deviceManagerGadgetPaneController.getFingerprintScannerInitializedLabel().visibleProperty());
		lblFingerprintScannerInitialized.managedProperty().bind(
						deviceManagerGadgetPaneController.getFingerprintScannerInitializedLabel().managedProperty());
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
		
		if(Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL ||
		   Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
		{
			txtUsernameLoginByPassword.setText(Context.getConfigManager().getProperty("dev.login.username"));
			txtPassword.setText(Context.getConfigManager().getProperty("dev.login.password"));
		}
		
		ClosureListener closureListener = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																	   .getClosureListener();
		Context.getBioKitManager().setClosureListener(closureListener);
		
		txtUsernameLoginByPassword.requestFocus();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		Preferences prefs = Preferences.userNodeForPackage(AppConstants.PREF_NODE_CLASS);
		String fingerprintPosition = prefs.get(AppConstants.LOGIN_FINGERPRINT_POSITION_PREF_NAME, null);
		
		if(fingerprintPosition != null && !fingerprintPosition.trim().isEmpty())
		{
			switch(fingerprintPosition)
			{
				case "1": currentFingerPosition = FingerPosition.RIGHT_THUMB; break;
				case "2": currentFingerPosition = FingerPosition.RIGHT_INDEX; break;
				case "3": currentFingerPosition = FingerPosition.RIGHT_MIDDLE; break;
				case "4": currentFingerPosition = FingerPosition.RIGHT_RING; break;
				case "5": currentFingerPosition = FingerPosition.RIGHT_LITTLE; break;
				case "6": currentFingerPosition = FingerPosition.LEFT_THUMB; break;
				case "7": currentFingerPosition = FingerPosition.LEFT_INDEX; break;
				case "8": currentFingerPosition = FingerPosition.LEFT_MIDDLE; break;
				case "9": currentFingerPosition = FingerPosition.LEFT_RING; break;
				case "10": currentFingerPosition = FingerPosition.LEFT_LITTLE; break;
				default: currentFingerPosition = FingerPosition.RIGHT_INDEX; break;
			}
		}
		else currentFingerPosition = FingerPosition.RIGHT_INDEX;
		
		activateFingerprint(currentFingerPosition);
	}
	
	@Override
	public void preReportNegativeTaskResponse()
	{
		txtPassword.clear();
		disableUiControls(false);
		
		if(tabLoginByPassword.isSelected()) txtUsernameLoginByPassword.requestFocus();
		else if(tabLoginByFingerprint.isSelected()) txtUsernameLoginByFingerprint.requestFocus();
	}
	
	@Override
	protected void onDetachingFromScene()
	{
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(null);
	}
	
	@FXML
	private void onLoginButtonClicked(ActionEvent event)
	{
		hideNotification();
		
		if(tabLoginByPassword.isSelected())
		{
			disableUiControls(true);
			
			loginMethod = LoginMethod.USERNAME_AND_PASSWORD;
			username = txtUsernameLoginByPassword.getText().trim();
			password = txtPassword.getText().trim();
			continueWorkflow();
		}
		else if(tabLoginByFingerprint.isSelected())
		{
			try
			{
				boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
				CaptureFingerprintDialogFxController captureFingerprintDialogFxController =
						DialogUtils.buildCustomDialogByFxml(Context.getCoreFxController().getStage(),
						                                    CaptureFingerprintDialogFxController.class, resources, rtl,
						                                    false);
				
				if(captureFingerprintDialogFxController != null)
				{
					captureFingerprintDialogFxController.setFingerPosition(currentFingerPosition);
					captureFingerprintDialogFxController.showDialogAndWait();
					
					fingerprint = captureFingerprintDialogFxController.getResult();
					
					if(fingerprint != null)
					{
						disableUiControls(true);
						
						loginMethod = LoginMethod.USERNAME_AND_FINGERPRINT;
						username = txtUsernameLoginByFingerprint.getText().trim();
						password = txtPassword.getText().trim();
						fingerPosition =  currentFingerPosition.getPosition();
						continueWorkflow();
					}
					else captureFingerprintDialogFxController.stopCapturingFingerprint();
				}
			}
			catch(Exception e)
			{
				String errorCode = LoginErrorCodes.C003_00009.getCode();
				String[] errorDetails =
									{"Failed to load (" + CaptureFingerprintDialogFxController.class.getName() + ")!"};
				Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
			}
		}
	}
	
	@FXML
	private void onChangePasswordButtonClicked(ActionEvent event)
	{
		hideNotification();
		
		try
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			ChangePasswordDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
					Context.getCoreFxController().getStage(), ChangePasswordDialogFxController.class, resources, rtl,
					false);
			
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
		catch(Exception e)
		{
			String errorCode = LoginErrorCodes.C003_00010.getCode();
			String[] errorDetails = {"Failed to load (" + ChangePasswordDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
		}
	}
	
	private void disableUiControls(boolean bool)
	{
		tabLoginByPassword.setDisable(bool);
		txtUsernameLoginByPassword.setDisable(bool);
		txtPassword.setDisable(bool);
		cboLanguage.setDisable(bool);
		
		tabLoginByFingerprint.setDisable(bool);
		txtUsernameLoginByFingerprint.setDisable(bool);
		btnFingerprintScannerAction.setDisable(bool);
		btnChangeFingerprint.setDisable(bool);
		
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
		stateBundle.putData("currentFingerPosition", currentFingerPosition);
	}
	
	@Override
	public void onLoadState(StateBundle stateBundle)
	{
		String username = stateBundle.getDate("username", String.class);
		String password = stateBundle.getDate("password", String.class);
		currentFingerPosition = stateBundle.getDate("currentFingerPosition", FingerPosition.class);
		
		txtUsernameLoginByPassword.setText(username);
		txtPassword.setText(password);
		cboLanguage.requestFocus();
	}
	
	@FXML
	private void onFingerprintScannerActionButtonClicked(MouseEvent mouseEvent)
	{
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.disableCollapsing(true);
		Set<Device> devices = new HashSet<>();
		devices.add(Device.FINGERPRINT_SCANNER);
		deviceManagerGadgetPaneController.showDeviceControls(devices);
		Pane devicesRunnerGadgetPane = deviceManagerGadgetPaneController.getRegionRootPane();
		GuiUtils.showNode(devicesRunnerGadgetPane, true);
		VBox vBox = new VBox(devicesRunnerGadgetPane);
		Stage dialogStage = DialogUtils.buildCustomDialog(Context.getCoreFxController().getStage(), "", vBox,
		                                                  rtl, true);
		
		deviceManagerGadgetPaneController.setFingerprintScannerInitializationListener(initialized ->
		{
		    if(initialized) dialogStage.close();
		});
		
		dialogStage.setOnShown(event -> initializeFingerprintScanner());
		dialogStage.setOnHidden(event ->
		{
			GuiUtils.showNode(devicesRunnerGadgetPane, false);
			Context.getCoreFxController().reattachDeviceRunnerGadgetPane();
			deviceManagerGadgetPaneController.setFingerprintScannerInitializationListener(null);
			deviceManagerGadgetPaneController.disableCollapsing(false);
		});
		dialogStage.showAndWait();
	}
	
	private void initializeFingerprintScanner()
	{
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		
		if(deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			if(!deviceManagerGadgetPaneController.isFingerprintScannerInitialized(FingerprintDeviceType.SINGLE))
			{
				boolean autoInitialize = "true".equals(
												Context.getConfigManager().getProperty("fingerprint.autoInitialize"));
				if(autoInitialize)
						deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SINGLE);
			}
		}
		else
		{
			boolean devicesRunnerAutoRun = "true".equals(
													Context.getConfigManager().getProperty("devicesRunner.autoRun"));
			if(devicesRunnerAutoRun) deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
	}
	
	@FXML
	private void onChangeFingerprintButtonClicked(ActionEvent actionEvent)
	{
		try
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			ChangeFingerprintDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
					Context.getCoreFxController().getStage(), ChangeFingerprintDialogFxController.class, resources, rtl,
					false);
			
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
		catch(Exception e)
		{
			String errorCode = LoginErrorCodes.C003_00011.getCode();
			String[] errorDetails = {"Failed to load (" + ChangeFingerprintDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
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
		
		// save for later usage
		Preferences prefs = Preferences.userNodeForPackage(AppConstants.PREF_NODE_CLASS);
		prefs.put(AppConstants.LOGIN_FINGERPRINT_POSITION_PREF_NAME, String.valueOf(fingerPosition.getPosition()));
	}
}