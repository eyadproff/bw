package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.controllers;

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
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.tasks.CriminalFingerprintsDeletionStatusCheckerWorkflowTask.Status;

@FxmlFile("showResult.fxml")
public class ShowResultPaneFxController extends WizardStepFxControllerBase
{
	public enum Request
	{
		DELETE_CRIMINAL_FINGERPRINTS,
		CHECK_CRIMINAL_FINGERPRINTS_DELETION,
		DELETE_CONVICTED_REPORTS,
		DONE
	}
	
	@Input(alwaysRequired = true) private long criminalBiometricsId;
	@Input(alwaysRequired = true) private int resultsTotalCount;
	@Input private Status criminalFingerprintsDeletionStatus;
	@Input private Boolean noFingerprintsFound;
	@Output private Request request;
	
	@FXML private Pane paneDeletingCriminalFingerprints;
	@FXML private Pane paneDeletingConvictedReports;
	@FXML private ProgressIndicator piDeletingCriminalFingerprints;
	@FXML private ProgressIndicator piDeletingConvictedReports;
	@FXML private ImageView ivDeletingCriminalFingerprintsSuccess;
	@FXML private ImageView ivDeletingCriminalFingerprintsFailure;
	@FXML private ImageView ivDeletingCriminalFingerprintsWarning;
	@FXML private ImageView ivDeletingConvictedReportsSuccess;
	@FXML private ImageView ivDeletingConvictedReportsFailure;
	@FXML private ImageView ivDeletingConvictedReportsWarning;
	@FXML private Label lblDeletingCriminalFingerprints;
	@FXML private Label lblDeletingConvictedReports;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		if(resultsTotalCount == 0)
		{
			lblDeletingConvictedReports.setText(resources.getString("label.noConvictedReportFound"));
			ivDeletingConvictedReportsWarning.setVisible(true);
		}
		
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
					
					if(resultsTotalCount > 0)
					{
						request = Request.DELETE_CONVICTED_REPORTS;
						continueWorkflow();
					}
					else
					{
						request = Request.DONE;
						btnRetry.setManaged(false);
						btnStartOver.setVisible(true);
					}
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
				if(criminalFingerprintsDeletionStatus == Status.PENDING)
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
					
					if(resultsTotalCount > 0)
					{
						lblDeletingConvictedReports.setText(resources.getString("label.deletingConvictedReports"));
						piDeletingConvictedReports.setVisible(true);
						request = Request.DELETE_CONVICTED_REPORTS;
						continueWorkflow();
					}
					else
					{
						request = Request.DONE;
						btnRetry.setManaged(false);
						btnStartOver.setVisible(true);
					}
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
		else if(request == Request.DELETE_CONVICTED_REPORTS)
		{
			piDeletingConvictedReports.setVisible(false);
			
			if(successfulResponse)
			{
				lblDeletingConvictedReports.setText(
											resources.getString("label.convictedReportsWereDeletedSuccessfully"));
				ivDeletingConvictedReportsSuccess.setVisible(true);
				request = Request.DONE;
				btnRetry.setManaged(false);
				btnStartOver.setVisible(true);
			}
			else
			{
				lblDeletingConvictedReports.setText(
											resources.getString("label.convictedReportsDeletionFailed"));
				ivDeletingConvictedReportsFailure.setVisible(true);
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
		else if(request == Request.DELETE_CONVICTED_REPORTS)
		{
			lblDeletingConvictedReports.setText(resources.getString("label.deletingConvictedReports"));
			ivDeletingConvictedReportsFailure.setVisible(false);
			piDeletingConvictedReports.setVisible(true);
		}
		
		btnStartOver.setVisible(false);
		btnRetry.setVisible(false);
		
		continueWorkflow();
	}
}