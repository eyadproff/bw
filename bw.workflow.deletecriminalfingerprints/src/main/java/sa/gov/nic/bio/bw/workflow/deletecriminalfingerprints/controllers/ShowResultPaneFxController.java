package sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.tasks.CriminalFingerprintsDeletionStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.utils.DeleteCriminalFingerprintsErrorCodes;

@FxmlFile("showResult.fxml")
public class ShowResultPaneFxController extends WizardStepFxControllerBase
{
	public enum Request
	{
		DELETE_CRIMINAL_FINGERPRINTS,
		CHECK_CRIMINAL_FINGERPRINTS_DELETION,
		DONE
	}
	
	@Input(alwaysRequired = true) private long criminalBiometricsId;
	@Input private Status criminalFingerprintsDeletionStatus;
	@Input private Boolean noFingerprintsFound;
	@Output private Request request;
	
	@FXML private Pane paneDeletingCriminalFingerprints;
	@FXML private ProgressIndicator piDeletingCriminalFingerprints;
	@FXML private ImageView ivDeletingCriminalFingerprintsSuccess;
	@FXML private ImageView ivDeletingCriminalFingerprintsFailure;
	@FXML private ImageView ivDeletingCriminalFingerprintsWarning;
	@FXML private Label lblDeletingCriminalFingerprints;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		txtCriminalBiometricsId.setText(String.valueOf(criminalBiometricsId));
		request = Request.DELETE_CRIMINAL_FINGERPRINTS;
		continueWorkflow();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(request == Request.DELETE_CRIMINAL_FINGERPRINTS)
		{
			if(successfulResponse)
			{
				if(noFingerprintsFound != null && noFingerprintsFound)
				{
					lblDeletingCriminalFingerprints.setText(resources.getString("label.noFingerprintsFound"));
					piDeletingCriminalFingerprints.setVisible(false);
					ivDeletingCriminalFingerprintsWarning.setVisible(true);
					request = Request.DONE;
					btnRetry.setManaged(false);
					btnStartOver.setVisible(true);
				}
				else
				{
					lblDeletingCriminalFingerprints.setText(
										resources.getString("label.checkingCriminalFingerprintsDeletionStatus"));
					request = Request.CHECK_CRIMINAL_FINGERPRINTS_DELETION;
					continueWorkflow();
				}
			}
			else
			{
				piDeletingCriminalFingerprints.setVisible(false);
				lblDeletingCriminalFingerprints.setText(resources.getString("label.fingerprintsDeletionFailed"));
				ivDeletingCriminalFingerprintsFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(true);
			}
		}
		else if(request == Request.CHECK_CRIMINAL_FINGERPRINTS_DELETION)
		{
			if(successfulResponse)
			{
				if(noFingerprintsFound != null && noFingerprintsFound)
				{
					lblDeletingCriminalFingerprints.setText(resources.getString("label.noFingerprintsFound"));
					piDeletingCriminalFingerprints.setVisible(false);
					ivDeletingCriminalFingerprintsWarning.setVisible(true);
					request = Request.DONE;
					btnRetry.setManaged(false);
					btnStartOver.setVisible(true);
				}
				else if(criminalFingerprintsDeletionStatus == Status.PENDING)
				{
					Context.getExecutorService().submit(() ->
					{
					    try
					    {
					        int seconds = Integer.parseInt(Context.getConfigManager().getProperty(
					                                "criminalFingerprintsDeletion.statusInquiry.checkEverySeconds"));
					        Thread.sleep(seconds * 1000);
					    }
					    catch(InterruptedException e)
					    {
					        e.printStackTrace();
					    }
					
					    Platform.runLater(this::continueWorkflow);
					});
				}
				else if(criminalFingerprintsDeletionStatus == Status.SUCCESS)
				{
					lblDeletingCriminalFingerprints.setText(
												resources.getString("label.fingerprintsWereDeletedSuccessfully"));
					piDeletingCriminalFingerprints.setVisible(false);
					ivDeletingCriminalFingerprintsSuccess.setVisible(true);
					request = Request.DONE;
					btnRetry.setManaged(false);
					btnStartOver.setVisible(true);
					
					showSuccessNotification(String.format(
							resources.getString("deleteCriminalFingerprints.success.message"),
							AppUtils.localizeNumbers(String.valueOf(criminalBiometricsId))));
				}
				else
				{
					lblDeletingCriminalFingerprints.setText(
													resources.getString("label.fingerprintsDeletionFailed"));
					piDeletingCriminalFingerprints.setVisible(false);
					ivDeletingCriminalFingerprintsFailure.setVisible(true);
					btnStartOver.setVisible(true);
					btnRetry.setVisible(true);
					
					String errorCode = DeleteCriminalFingerprintsErrorCodes.C017_00001.getCode();
					String[] errorDetails = {"got invalid status on checking for fingerprints deletion!"};
					Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails, getTabIndex());
				}
			}
			else
			{
				lblDeletingCriminalFingerprints.setText(resources.getString("label.fingerprintsDeletionFailed"));
				piDeletingCriminalFingerprints.setVisible(false);
				ivDeletingCriminalFingerprintsFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(true);
			}
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		if(request == Request.DELETE_CRIMINAL_FINGERPRINTS)
		{
			lblDeletingCriminalFingerprints.setText(resources.getString("label.deletingCriminalFingerprints"));
			ivDeletingCriminalFingerprintsFailure.setVisible(false);
			piDeletingCriminalFingerprints.setVisible(true);
		}
		else if(request == Request.CHECK_CRIMINAL_FINGERPRINTS_DELETION)
		{
			lblDeletingCriminalFingerprints.setText(
										resources.getString("label.checkingCriminalFingerprintsDeletionStatus"));
			ivDeletingCriminalFingerprintsFailure.setVisible(false);
			piDeletingCriminalFingerprints.setVisible(true);
		}
		
		btnStartOver.setVisible(false);
		btnRetry.setVisible(false);
		
		continueWorkflow();
	}
}