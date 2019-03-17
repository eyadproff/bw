package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.tasks.CriminalFingerprintsDeletionStatusCheckerWorkflowTask.Status;

@FxmlFile("deletionProgress.fxml")
public class DeletionProgressPaneFxController extends WizardStepFxControllerBase
{
	public enum Request
	{
		DELETE_CRIMINAL_FINGERPRINTS,
		CHECK_CRIMINAL_FINGERPRINTS_DELETION,
		DELETE_CONVICTED_REPORTS
	}
	
	@Input private Status criminalFingerprintsDeletionStatus;
	@Output	private Request request;
	
	@FXML private Pane paneDeletingCriminalFingerprints;
	@FXML private Pane paneCheckingCriminalFingerprintsDeletionStatus;
	@FXML private Pane paneDeletingConvictedReports;
	@FXML private ProgressIndicator piDeletingCriminalFingerprints;
	@FXML private ProgressIndicator piCheckingCriminalFingerprintsDeletionStatus;
	@FXML private ProgressIndicator piDeletingConvictedReports;
	@FXML private ImageView ivDeletingCriminalFingerprintsSuccess;
	@FXML private ImageView ivDeletingCriminalFingerprintsFailure;
	@FXML private ImageView ivCheckingCriminalFingerprintsDeletionStatusSuccess;
	@FXML private ImageView ivCheckingCriminalFingerprintsDeletionStatusFailure;
	@FXML private ImageView ivDeletingConvictedReportsSuccess;
	@FXML private ImageView ivDeletingConvictedReportsFailure;
	@FXML private Label lblDeletingCriminalFingerprints;
	@FXML private Label lblCheckingCriminalFingerprintsDeletionStatus;
	@FXML private Label lblDeletingConvictedReports;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		request = Request.DELETE_CRIMINAL_FINGERPRINTS;
		continueWorkflow();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(request == Request.DELETE_CRIMINAL_FINGERPRINTS)
		{
			piDeletingCriminalFingerprints.setVisible(false);
			
			if(successfulResponse)
			{
				ivDeletingCriminalFingerprintsSuccess.setVisible(true);
				request = Request.CHECK_CRIMINAL_FINGERPRINTS_DELETION;
				piCheckingCriminalFingerprintsDeletionStatus.setVisible(true);
				continueWorkflow();
			}
			else
			{
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
					piCheckingCriminalFingerprintsDeletionStatus.setVisible(false);
					ivCheckingCriminalFingerprintsDeletionStatusSuccess.setVisible(true);
					request = Request.DELETE_CONVICTED_REPORTS;
					piDeletingConvictedReports.setVisible(true);
					continueWorkflow();
				}
			}
			else
			{
				piCheckingCriminalFingerprintsDeletionStatus.setVisible(false);
				ivCheckingCriminalFingerprintsDeletionStatusFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(true);
			}
		}
		else if(request == Request.DELETE_CONVICTED_REPORTS)
		{
			piDeletingConvictedReports.setVisible(false);
			
			if(successfulResponse)
			{
				ivDeletingConvictedReportsSuccess.setVisible(true);
				goNext();
			}
			else
			{
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
			ivDeletingCriminalFingerprintsFailure.setVisible(false);
			piDeletingCriminalFingerprints.setVisible(true);
		}
		else if(request == Request.CHECK_CRIMINAL_FINGERPRINTS_DELETION)
		{
			ivCheckingCriminalFingerprintsDeletionStatusFailure.setVisible(false);
			piCheckingCriminalFingerprintsDeletionStatus.setVisible(true);
		}
		else if(request == Request.DELETE_CONVICTED_REPORTS)
		{
			ivDeletingConvictedReportsFailure.setVisible(false);
			piDeletingConvictedReports.setVisible(true);
		}
		
		btnStartOver.setVisible(false);
		btnRetry.setVisible(false);
		
		continueWorkflow();
	}
}