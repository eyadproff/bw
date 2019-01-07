package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;

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
	
	public enum Result
	{
		CHECKING_FINGERPRINTS_SUCCESS,
		CHECKING_FINGERPRINTS_PENDING
	}
	
	@Input private Boolean registerFingerprints;
	@Input private Result result;
	@Output	private Request request;
	
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
			GuiUtils.showNode(piGeneratingNewCriminalBiometricsId, true);
			request = Request.GENERATING_NEW_CRIMINAL_BIOMETRICS_ID;
		}
		else
		{
			GuiUtils.showNode(piSubmittingConvictedReport, true);
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
				GuiUtils.showNode(piGeneratingNewCriminalBiometricsId, false);
				GuiUtils.showNode(ivGeneratingNewCriminalBiometricsIdSuccess, true);
				
				request = Request.SUBMITTING_FINGERPRINTS;
				continueWorkflow();
			}
			else
			{
				GuiUtils.showNode(piGeneratingNewCriminalBiometricsId, false);
				GuiUtils.showNode(ivGeneratingNewCriminalBiometricsIdFailure, true);
				GuiUtils.showNode(btnStartOver, true);
				GuiUtils.showNode(btnRetry, true);
			}
		}
		else if(request == Request.SUBMITTING_FINGERPRINTS)
		{
			if(successfulResponse)
			{
				GuiUtils.showNode(piSubmittingFingerprints, false);
				GuiUtils.showNode(ivSubmittingFingerprintsSuccess, true);
				
				request = Request.CHECKING_FINGERPRINTS;
				continueWorkflow();
			}
			else
			{
				GuiUtils.showNode(piSubmittingFingerprints, false);
				GuiUtils.showNode(ivSubmittingFingerprintsFailure, true);
				GuiUtils.showNode(btnStartOver, true);
				GuiUtils.showNode(btnRetry, true);
			}
		}
		else if(request == Request.CHECKING_FINGERPRINTS)
		{
			if(successfulResponse)
			{
				if(result == Result.CHECKING_FINGERPRINTS_PENDING)
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
				else if(result == Result.CHECKING_FINGERPRINTS_SUCCESS)
				{
					GuiUtils.showNode(piCheckingFingerprints, false);
					GuiUtils.showNode(ivCheckingFingerprintsSuccess, true);
					
					request = Request.SUBMITTING_CONVICTED_REPORT;
				}
				
				continueWorkflow();
			}
			else
			{
				GuiUtils.showNode(piCheckingFingerprints, false);
				GuiUtils.showNode(ivCheckingFingerprintsFailure, true);
				GuiUtils.showNode(btnStartOver, true);
				GuiUtils.showNode(btnRetry, true);
			}
		}
		else if(request == Request.SUBMITTING_CONVICTED_REPORT)
		{
			if(successfulResponse)
			{
				GuiUtils.showNode(piSubmittingConvictedReport, false);
				GuiUtils.showNode(ivSubmittingConvictedReportSuccess, true);
				goNext();
			}
			else
			{
				GuiUtils.showNode(piSubmittingConvictedReport, false);
				GuiUtils.showNode(ivSubmittingConvictedReportFailure, true);
				GuiUtils.showNode(btnStartOver, true);
				GuiUtils.showNode(btnRetry, true);
			}
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		if(request == Request.GENERATING_NEW_CRIMINAL_BIOMETRICS_ID)
		{
			GuiUtils.showNode(ivGeneratingNewCriminalBiometricsIdFailure, false);
			GuiUtils.showNode(piGeneratingNewCriminalBiometricsId, true);
		}
		else if(request == Request.SUBMITTING_FINGERPRINTS)
		{
			GuiUtils.showNode(ivSubmittingFingerprintsFailure, false);
			GuiUtils.showNode(piSubmittingFingerprints, true);
		}
		else if(request == Request.CHECKING_FINGERPRINTS)
		{
			GuiUtils.showNode(ivCheckingFingerprintsFailure, false);
			GuiUtils.showNode(piCheckingFingerprints, true);
		}
		else if(request == Request.SUBMITTING_CONVICTED_REPORT)
		{
			GuiUtils.showNode(ivSubmittingConvictedReportFailure, false);
			GuiUtils.showNode(piSubmittingConvictedReport, true);
		}
		
		continueWorkflow();
	}
}