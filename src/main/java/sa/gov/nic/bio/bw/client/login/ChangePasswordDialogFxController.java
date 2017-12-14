package sa.gov.nic.bio.bw.client.login;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.CoreFxController;
import sa.gov.nic.bio.bw.client.core.interfaces.AttachableController;
import sa.gov.nic.bio.bw.client.login.tasks.ChangePasswordTask;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChangePasswordDialogFxController implements AttachableController
{
	private static final Logger LOGGER = Logger.getLogger(ChangePasswordDialogFxController.class.getName());
	
	@FXML private Dialog<ButtonType> dialog;
	@FXML private TextField txtUsername;
	@FXML private TextField txtCurrentPassword;
	@FXML private TextField txtNewPassword;
	@FXML private TextField txtNewPasswordConfirm;
	@FXML private StackPane resultPane;
	@FXML private ProgressIndicator piChangePassword;
	@FXML private TextFlow tfResultMessage;
	@FXML private ImageView ivWarningIcon;
	@FXML private ImageView ivErrorIcon;
	@FXML private Text txtResultMessage;
	@FXML private ButtonType btChange;
	@FXML private ButtonType btCancel;
	
	private Button btnChange;
	private Button btnCancel;
	private BooleanProperty btnChangeDisabledProperty = new SimpleBooleanProperty(false);
	
	private boolean passwordChangedSuccessfully = false;
	private CoreFxController coreFxController;
	private  ResourceBundle errorsBundle;
	private ResourceBundle messagesBundle;
	
	@FXML
	private void initialize()
	{
		dialog.setOnShown(event ->
		{
			btnChange = (Button) dialog.getDialogPane().lookupButton(btChange);
			btnCancel = (Button) dialog.getDialogPane().lookupButton(btCancel);
			
			btnChange.setDefaultButton(true);
			btnCancel.setCancelButton(true);
			
			btnChange.addEventHandler(KeyEvent.KEY_PRESSED, e ->
			{
				if(e.getCode() == KeyCode.ENTER)
				{
					btnChange.fire();
					e.consume();
				}
			});
			btnCancel.addEventHandler(KeyEvent.KEY_PRESSED, e ->
			{
				if(e.getCode() == KeyCode.ENTER)
				{
					btnCancel.fire();
					e.consume();
				}
			});
			
			BooleanBinding booleanBinding = txtUsername.textProperty().isEmpty()
										.or(txtCurrentPassword.textProperty().isEmpty())
										.or(txtNewPassword.textProperty().isEmpty())
										.or(txtNewPasswordConfirm.textProperty().isEmpty())
										.or(btnChangeDisabledProperty);
			
			btnChange.disableProperty().bind(booleanBinding);
			btnChange.addEventFilter(ActionEvent.ACTION, event1 ->
			{
				resultPane.setVisible(true);
				resultPane.setManaged(true);
				
				// validate inputs
				if(txtNewPassword.getText().equals(txtNewPasswordConfirm.getText()))
				{
					showProgress(true);
					
					String username = txtUsername.getText();
					String oldPassword = txtUsername.getText();
					String newPassword = txtUsername.getText();
					
					
					ChangePasswordTask task = new ChangePasswordTask(username, oldPassword, newPassword);
					task.setOnSucceeded(event2 ->
					{
						LOGGER.info("The password of (" + username + ") has been changed successfully!");
					    passwordChangedSuccessfully = true;
						showProgress(false);
						dialog.setResult(btChange); // the dialog will not be closed unless it has a result
					    dialog.close();
					});
					task.setOnFailed(event2 ->
					{
						showProgress(false);
						String errorCode = task.getErrorCode();
						int httpCode = task.getHttpCode();
						String apiUrl = task.getApiUrl();
						Exception exception = (Exception) task.getException();
						
						if(errorCode == null || errorCode.isEmpty())
						{
							String code = "C002-00018";
							String guiErrorMessage = coreFxController.getErrorsBundle().getString(code);
							String logErrorMessage = coreFxController.getErrorsBundle().getString(code + ".internal");
							logErrorMessage = String.format(logErrorMessage, apiUrl, String.valueOf(httpCode));
							
							showErrorMessage(guiErrorMessage);
							LOGGER.log(Level.SEVERE, logErrorMessage, exception);
						}
						else if(errorCode.startsWith("C"))
						{
							String guiErrorMessage = coreFxController.getErrorsBundle().getString(errorCode);
							String logErrorMessage = coreFxController.getErrorsBundle().getString(errorCode + ".internal");
							
							guiErrorMessage = String.format(guiErrorMessage, httpCode);
							logErrorMessage = String.format(logErrorMessage, httpCode);
							
							showErrorMessage(guiErrorMessage);
							LOGGER.log(Level.SEVERE, logErrorMessage, exception);
						}
						else if(errorCode.startsWith("B"))
						{
							String message = errorsBundle.getString(errorCode);
							showWarningMessage(message);
						}
						else if(errorCode.startsWith("S"))
						{
							String code = "S000-00000";
							String guiErrorMessage = coreFxController.getErrorsBundle().getString(code);
							String logErrorMessage = coreFxController.getErrorsBundle().getString(code + ".internal");
							
							guiErrorMessage = String.format(guiErrorMessage, errorCode);
							logErrorMessage = String.format(logErrorMessage, errorCode);
							
							showErrorMessage(guiErrorMessage);
							LOGGER.severe(logErrorMessage);
						}
						
						txtUsername.requestFocus();
					});
					
					Context.getExecutorService().submit(task);
				}
				else
				{
					String message = messagesBundle.getString("changePassword.newPasswordConfirm.notMatch");
					showWarningMessage(message);
					txtNewPassword.requestFocus();
				}
				
				event1.consume(); // prevent the bubble up
			});
			
			dialog.getDialogPane().getScene().getWindow().sizeToScene();
		});
	}
	
	public void showDialogAndWait()
	{
		dialog.showAndWait();
	}
	
	public void setUsernameAndPassword(String username, String password)
	{
		txtUsername.setText(username);
		if(username != null && !username.isEmpty()) txtCurrentPassword.setText(password);
	}
	
	public void requestFocus()
	{
		if(txtUsername.getText().isEmpty()) txtUsername.requestFocus();
		else if(txtCurrentPassword.getText().isEmpty()) txtCurrentPassword.requestFocus();
		else txtNewPassword.requestFocus();
	}
	
	public boolean isPasswordChangedSuccessfully()
	{
		return passwordChangedSuccessfully;
	}
	
	private void showProgress(boolean bool)
	{
		txtUsername.setDisable(bool);
		txtCurrentPassword.setDisable(bool);
		txtNewPassword.setDisable(bool);
		txtNewPasswordConfirm.setDisable(bool);
		btnCancel.setDisable(bool);
		btnChangeDisabledProperty.set(bool);
		
		piChangePassword.setVisible(bool);
		piChangePassword.setManaged(bool);
		tfResultMessage.setVisible(!bool);
		tfResultMessage.setManaged(!bool);
		
		dialog.getDialogPane().getScene().getWindow().sizeToScene();
	}
	
	private void showErrorMessage(String message)
	{
		tfResultMessage.setVisible(true);
		tfResultMessage.setManaged(true);
		ivErrorIcon.setVisible(true);
		ivErrorIcon.setManaged(true);
		ivWarningIcon.setVisible(false);
		ivWarningIcon.setManaged(false);
		txtResultMessage.setText(message);
		dialog.getDialogPane().getScene().getWindow().sizeToScene();
	}
	
	private void showWarningMessage(String message)
	{
		tfResultMessage.setVisible(true);
		tfResultMessage.setManaged(true);
		ivErrorIcon.setVisible(false);
		ivErrorIcon.setManaged(false);
		ivWarningIcon.setVisible(true);
		ivWarningIcon.setManaged(true);
		txtResultMessage.setText(message);
		dialog.getDialogPane().getScene().getWindow().sizeToScene();
	}
	
	@Override
	public void attachCoreFxController(CoreFxController coreFxController)
	{
		this.coreFxController = coreFxController;
	}
	
	@Override
	public void attachInitialResources(ResourceBundle labelsBundle, ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon)
	{
		this.errorsBundle = errorsBundle;
		this.messagesBundle = messagesBundle;
	}
}