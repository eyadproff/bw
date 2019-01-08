package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

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
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.CriminalFingerprintsStatusCheckerWorkflowTask.Status;

@FxmlFile("registeringConvictedReport.fxml")
public class RegisteringConvictedReportPaneFxController extends WizardStepFxControllerBase
{
	public enum Request
	{
		GENERATING_NEW_CRIMINAL_BIOMETRICS_ID,
		SUBMITTING_FINGERPRINTS,
		CHECKING_FINGERPRINTS,
		SUBMITTING_CONVICTED_REPORT
	}
	
	@Input private Boolean registerFingerprints;
	@Input private Status criminalFingerprintsRegistrationStatus;
	@Output	private Request request;
	
	@FXML private Pane paneGeneratingNewCriminalBiometricsIdStatus;
	@FXML private Pane paneSubmittingFingerprintsStatus;
	@FXML private Pane paneCheckingFingerprintsStatus;
	@FXML private Pane paneSubmittingConvictedReportStatus;
	@FXML private ProgressIndicator piGeneratingNewCriminalBiometricsId;
	@FXML private ProgressIndicator piSubmittingFingerprints;
	@FXML private ProgressIndicator piCheckingFingerprints;
	@FXML private ProgressIndicator piSubmittingConvictedReport;
	@FXML private ImageView ivGeneratingNewCriminalBiometricsIdSuccess;
	@FXML private ImageView ivGeneratingNewCriminalBiometricsIdFailure;
	@FXML private ImageView ivSubmittingFingerprintsSuccess;
	@FXML private ImageView ivSubmittingFingerprintsFailure;
	@FXML private ImageView ivCheckingFingerprintsSuccess;
	@FXML private ImageView ivCheckingFingerprintsFailure;
	@FXML private ImageView ivSubmittingConvictedReportSuccess;
	@FXML private ImageView ivSubmittingConvictedReportFailure;
	@FXML private Label lblGeneratingNewCriminalBiometricsId;
	@FXML private Label lblSubmittingFingerprints;
	@FXML private Label lblCheckingFingerprints;
	@FXML private Label lblSubmittingConvictedReport;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		if(registerFingerprints != null && registerFingerprints)
		{
			GuiUtils.showNode(lblGeneratingNewCriminalBiometricsId, true);
			GuiUtils.showNode(lblSubmittingFingerprints, true);
			GuiUtils.showNode(lblCheckingFingerprints, true);
			GuiUtils.showNode(paneGeneratingNewCriminalBiometricsIdStatus, true);
			GuiUtils.showNode(paneSubmittingFingerprintsStatus, true);
			GuiUtils.showNode(paneCheckingFingerprintsStatus, true);
			piGeneratingNewCriminalBiometricsId.setVisible(true);
			request = Request.GENERATING_NEW_CRIMINAL_BIOMETRICS_ID;
		}
		else
		{
			piSubmittingConvictedReport.setVisible(true);
			request = Request.SUBMITTING_CONVICTED_REPORT;
		}
		
		continueWorkflow();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(request == Request.GENERATING_NEW_CRIMINAL_BIOMETRICS_ID)
		{
			if(successfulResponse)
			{
				piGeneratingNewCriminalBiometricsId.setVisible(false);
				ivGeneratingNewCriminalBiometricsIdSuccess.setVisible(true);
				
				request = Request.SUBMITTING_FINGERPRINTS;
				continueWorkflow();
			}
			else
			{
				piGeneratingNewCriminalBiometricsId.setVisible(false);
				ivGeneratingNewCriminalBiometricsIdFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(true);
			}
		}
		else if(request == Request.SUBMITTING_FINGERPRINTS)
		{
			if(successfulResponse)
			{
				piSubmittingFingerprints.setVisible(false);
				ivSubmittingFingerprintsSuccess.setVisible(true);
				
				request = Request.CHECKING_FINGERPRINTS;
				continueWorkflow();
			}
			else
			{
				piSubmittingFingerprints.setVisible(false);
				ivSubmittingFingerprintsFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(true);
			}
		}
		else if(request == Request.CHECKING_FINGERPRINTS)
		{
			if(successfulResponse)
			{
				if(criminalFingerprintsRegistrationStatus == Status.PENDING)
				{
					try
					{
						int seconds = Integer.parseInt(Context.getConfigManager().getProperty(
																"registerConvictedReport.inquiry.checkEverySeconds"));
						Thread.sleep(seconds * 1000);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				else if(criminalFingerprintsRegistrationStatus == Status.SUCCESS)
				{
					piCheckingFingerprints.setVisible(false);
					ivCheckingFingerprintsSuccess.setVisible(true);
					
					request = Request.SUBMITTING_CONVICTED_REPORT;
				}
				
				continueWorkflow();
			}
			else
			{
				piCheckingFingerprints.setVisible(false);
				ivCheckingFingerprintsFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(true);
			}
		}
		else if(request == Request.SUBMITTING_CONVICTED_REPORT)
		{
			if(successfulResponse)
			{
				piSubmittingConvictedReport.setVisible(false);
				ivSubmittingConvictedReportSuccess.setVisible(true);
				goNext();
			}
			else
			{
				piSubmittingConvictedReport.setVisible(false);
				ivSubmittingConvictedReportFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(true);
			}
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		if(request == Request.GENERATING_NEW_CRIMINAL_BIOMETRICS_ID)
		{
			ivGeneratingNewCriminalBiometricsIdFailure.setVisible(false);
			piGeneratingNewCriminalBiometricsId.setVisible(true);
		}
		else if(request == Request.SUBMITTING_FINGERPRINTS)
		{
			ivSubmittingFingerprintsFailure.setVisible(false);
			piSubmittingFingerprints.setVisible(true);
		}
		else if(request == Request.CHECKING_FINGERPRINTS)
		{
			ivCheckingFingerprintsFailure.setVisible(false);
			piCheckingFingerprints.setVisible(true);
		}
		else if(request == Request.SUBMITTING_CONVICTED_REPORT)
		{
			ivSubmittingConvictedReportFailure.setVisible(false);
			piSubmittingConvictedReport.setVisible(true);
		}
		
		btnStartOver.setVisible(false);
		btnRetry.setVisible(false);
		
		continueWorkflow();
	}
}