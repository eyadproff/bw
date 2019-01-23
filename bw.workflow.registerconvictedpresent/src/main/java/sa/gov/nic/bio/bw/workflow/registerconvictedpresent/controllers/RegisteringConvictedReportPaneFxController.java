package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
		GENERATE_NEW_CRIMINAL_BIOMETRICS_ID,
		SUBMIT_FINGERPRINTS,
		CHECK_FINGERPRINTS,
		SUBMIT_CONVICTED_REPORT,
		EXCHANGE_CONVICTED_REPORT
	}
	
	@Input private Boolean registerFingerprints;
	@Input private Boolean exchangeConvictedReport;
	@Input private Status criminalFingerprintsRegistrationStatus;
	@Output	private Request request;
	
	private boolean disableRetryButtonForever = false;
	
	@FXML private Pane paneGeneratingNewCriminalBiometricsIdStatus;
	@FXML private Pane paneSubmittingFingerprintsStatus;
	@FXML private Pane paneCheckingFingerprintsStatus;
	@FXML private Pane paneSubmittingConvictedReportStatus;
	@FXML private StackPane paneExchangingConvictedReportStatus;
	@FXML private ProgressIndicator piGeneratingNewCriminalBiometricsId;
	@FXML private ProgressIndicator piSubmittingFingerprints;
	@FXML private ProgressIndicator piCheckingFingerprints;
	@FXML private ProgressIndicator piSubmittingConvictedReport;
	@FXML private ProgressIndicator piExchangingConvictedReport;
	@FXML private ImageView ivGeneratingNewCriminalBiometricsIdSuccess;
	@FXML private ImageView ivGeneratingNewCriminalBiometricsIdFailure;
	@FXML private ImageView ivSubmittingFingerprintsSuccess;
	@FXML private ImageView ivSubmittingFingerprintsFailure;
	@FXML private ImageView ivCheckingFingerprintsSuccess;
	@FXML private ImageView ivCheckingFingerprintsFailure;
	@FXML private ImageView ivSubmittingConvictedReportSuccess;
	@FXML private ImageView ivSubmittingConvictedReportFailure;
	@FXML private ImageView ivExchangingConvictedReportSuccess;
	@FXML private ImageView ivExchangingConvictedReportFailure;
	@FXML private Label lblGeneratingNewCriminalBiometricsId;
	@FXML private Label lblSubmittingFingerprints;
	@FXML private Label lblCheckingFingerprints;
	@FXML private Label lblSubmittingConvictedReport;
	@FXML private Label lblExchangingConvictedReport;
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
			request = Request.GENERATE_NEW_CRIMINAL_BIOMETRICS_ID;
		}
		else
		{
			piSubmittingConvictedReport.setVisible(true);
			request = Request.SUBMIT_CONVICTED_REPORT;
		}
		
		if(exchangeConvictedReport != null && exchangeConvictedReport)
		{
			GuiUtils.showNode(lblExchangingConvictedReport, true);
			GuiUtils.showNode(paneExchangingConvictedReportStatus, true);
		}
		
		continueWorkflow();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(request == Request.GENERATE_NEW_CRIMINAL_BIOMETRICS_ID)
		{
			piGeneratingNewCriminalBiometricsId.setVisible(false);
			
			if(successfulResponse)
			{
				ivGeneratingNewCriminalBiometricsIdSuccess.setVisible(true);
				request = Request.SUBMIT_FINGERPRINTS;
				piSubmittingFingerprints.setVisible(true);
				continueWorkflow();
			}
			else
			{
				ivGeneratingNewCriminalBiometricsIdFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(!disableRetryButtonForever);
			}
		}
		else if(request == Request.SUBMIT_FINGERPRINTS)
		{
			piSubmittingFingerprints.setVisible(false);
			
			if(successfulResponse)
			{
				ivSubmittingFingerprintsSuccess.setVisible(true);
				request = Request.CHECK_FINGERPRINTS;
				piCheckingFingerprints.setVisible(true);
				continueWorkflow();
			}
			else
			{
				ivSubmittingFingerprintsFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(!disableRetryButtonForever);
			}
		}
		else if(request == Request.CHECK_FINGERPRINTS)
		{
			if(successfulResponse)
			{
				if(criminalFingerprintsRegistrationStatus == Status.PENDING)
				{
					Context.getExecutorService().submit(() ->
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
						
						Platform.runLater(this::continueWorkflow);
					});
					
				}
				else if(criminalFingerprintsRegistrationStatus == Status.SUCCESS)
				{
					piCheckingFingerprints.setVisible(false);
					ivCheckingFingerprintsSuccess.setVisible(true);
					request = Request.SUBMIT_CONVICTED_REPORT;
					piSubmittingConvictedReport.setVisible(true);
					continueWorkflow();
				}
			}
			else
			{
				piCheckingFingerprints.setVisible(false);
				ivCheckingFingerprintsFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(!disableRetryButtonForever);
			}
		}
		else if(request == Request.SUBMIT_CONVICTED_REPORT)
		{
			piSubmittingConvictedReport.setVisible(false);
			
			if(successfulResponse)
			{
				ivSubmittingConvictedReportSuccess.setVisible(true);
				
				if(exchangeConvictedReport != null && exchangeConvictedReport)
				{
					request = Request.EXCHANGE_CONVICTED_REPORT;
					piExchangingConvictedReport.setVisible(true);
					continueWorkflow();
				}
				else goNext();
			}
			else
			{
				ivSubmittingConvictedReportFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(!disableRetryButtonForever);
			}
		}
		else if(request == Request.EXCHANGE_CONVICTED_REPORT)
		{
			piExchangingConvictedReport.setVisible(false);
			
			if(successfulResponse)
			{
				ivExchangingConvictedReportSuccess.setVisible(true);
				goNext();
			}
			else
			{
				ivExchangingConvictedReportFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(!disableRetryButtonForever);
			}
		}
	}
	
	@Override
	protected void reportNegativeTaskResponse(String errorCode, Throwable exception, String[] errorDetails)
	{
		if("B003-0042".equals(errorCode) || "B003-0044".equals(errorCode)) disableRetryButtonForever = true;
		
		super.reportNegativeTaskResponse(errorCode, exception, errorDetails);
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		if(request == Request.GENERATE_NEW_CRIMINAL_BIOMETRICS_ID)
		{
			ivGeneratingNewCriminalBiometricsIdFailure.setVisible(false);
			piGeneratingNewCriminalBiometricsId.setVisible(true);
		}
		else if(request == Request.SUBMIT_FINGERPRINTS)
		{
			ivSubmittingFingerprintsFailure.setVisible(false);
			piSubmittingFingerprints.setVisible(true);
		}
		else if(request == Request.CHECK_FINGERPRINTS)
		{
			ivCheckingFingerprintsFailure.setVisible(false);
			piCheckingFingerprints.setVisible(true);
		}
		else if(request == Request.SUBMIT_CONVICTED_REPORT)
		{
			ivSubmittingConvictedReportFailure.setVisible(false);
			piSubmittingConvictedReport.setVisible(true);
		}
		else if(request == Request.EXCHANGE_CONVICTED_REPORT)
		{
			ivExchangingConvictedReportFailure.setVisible(false);
			piExchangingConvictedReport.setVisible(true);
		}
		
		btnStartOver.setVisible(false);
		btnRetry.setVisible(false);
		
		continueWorkflow();
	}
}