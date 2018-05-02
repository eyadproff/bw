package sa.gov.nic.bio.bw.client.login;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.RegionFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.login.tasks.ChangePasswordTask;
import sa.gov.nic.bio.bw.client.login.utils.LoginErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.logging.Logger;

public class ChangePasswordDialogFxController extends RegionFxControllerBase
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
	
	@Override
	protected void initialize()
	{
		dialog.setOnShown(event ->
		{
			btnChange = (Button) dialog.getDialogPane().lookupButton(btChange);
			btnCancel = (Button) dialog.getDialogPane().lookupButton(btCancel);
			
			btnChange.setDefaultButton(true);
			btnCancel.setCancelButton(true);
			
			GuiUtils.makeButtonClickableByPressingEnter(btnChange);
			GuiUtils.makeButtonClickableByPressingEnter(btnCancel);
			
			BooleanBinding booleanBinding = txtUsername.textProperty().isEmpty()
										.or(txtCurrentPassword.textProperty().isEmpty())
										.or(txtNewPassword.textProperty().isEmpty())
										.or(txtNewPasswordConfirm.textProperty().isEmpty())
										.or(btnChangeDisabledProperty);
			
			btnChange.disableProperty().bind(booleanBinding);
			btnChange.addEventFilter(ActionEvent.ACTION, event1 ->
			{
				GuiUtils.showNode(resultPane, true);
				
				// validate inputs
				if(txtNewPassword.getText().equals(txtNewPasswordConfirm.getText()))
				{
					showProgress(true);
					
					String username = txtUsername.getText();
					String oldPassword = txtCurrentPassword.getText();
					String newPassword = txtNewPassword.getText();
					
					ChangePasswordTask task = new ChangePasswordTask(username, oldPassword, newPassword);
					task.setOnSucceeded(event2 ->
					{
						ServiceResponse<Boolean> serviceResponse;
						try
						{
							serviceResponse = task.get();
						}
						catch(Exception e)
						{
							String errorCode = LoginErrorCodes.C003_00001.getCode();
							String[] errorDetails = {"Failed to get a response when changing the password!"};
							Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
							return;
						}
						
						if(serviceResponse.isSuccess())
						{
							LOGGER.info("The password of (" + username + ") has been changed successfully!");
							passwordChangedSuccessfully = true;
							showProgress(false);
							dialog.setResult(btChange); // the dialog will not be closed unless it has a result
							dialog.close();
						}
						else
						{
							String errorCode = serviceResponse.getErrorCode();
							Exception exception = serviceResponse.getException();
							String[] errorDetails = serviceResponse.getErrorDetails();
							
							if(errorCode.startsWith("B") || errorCode.startsWith("N")) // business error
							{
								// no exceptions/errorDetails in case of business error
								
								String guiErrorMessage = Context.getErrorsBundle().getString(errorCode);
								String logErrorMessage = Context.getErrorsBundle()
																.getString(errorCode + ".internal");
								
								LOGGER.info(logErrorMessage);
								showWarningMessage(guiErrorMessage);
							}
							else // client error, server error, or unknown error
							{
								Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
							}
							
							showProgress(false);
							txtUsername.requestFocus();
						}
					});
					task.setOnFailed(event2 ->
					{
						showProgress(false);
						String errorCode = LoginErrorCodes.C003_00002.getCode();
						String[] errorDetails = {"Failure when executing ChangePasswordTask!"};
						Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
					});
					
					Context.getExecutorService().submit(task);
				}
				else
				{
					String message = resources.getString("changePassword.newPasswordConfirm.notMatch");
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
		
		GuiUtils.showNode(piChangePassword, bool);
		GuiUtils.showNode(tfResultMessage, false);
		
		dialog.getDialogPane().getScene().getWindow().sizeToScene();
	}
	
	private void showWarningMessage(String message)
	{
		GuiUtils.showNode(tfResultMessage, true);
		GuiUtils.showNode(ivErrorIcon, false);
		GuiUtils.showNode(ivWarningIcon, true);
		
		txtResultMessage.setText(message);
		dialog.getDialogPane().getScene().getWindow().sizeToScene();
	}
}