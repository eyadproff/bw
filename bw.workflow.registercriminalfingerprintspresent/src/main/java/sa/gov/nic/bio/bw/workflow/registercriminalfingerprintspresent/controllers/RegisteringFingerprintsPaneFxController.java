package sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers;

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
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.CriminalFingerprintsStatusCheckerWorkflowTask.Status;

@FxmlFile("registeringFingerprints.fxml")
public class RegisteringFingerprintsPaneFxController extends WizardStepFxControllerBase
{
	public enum Request
	{
		GENERATE_NEW_CRIMINAL_BIOMETRICS_ID,
		SUBMIT_FINGERPRINTS,
		CHECK_FINGERPRINTS
	}
	
	@Input protected Long criminalBiometricsId;
	@Input private Status criminalFingerprintsRegistrationStatus;
	@Output	private Request request;
	
	private boolean disableRetryButtonForever = false;
	
	@FXML private Pane paneGeneratingNewCriminalBiometricsIdStatus;
	@FXML private Pane paneSubmittingFingerprints;
	@FXML private Pane paneCheckingFingerprintsStatus;
	@FXML private ProgressIndicator piGeneratingNewCriminalBiometricsId;
	@FXML private ProgressIndicator piSubmittingFingerprints;
	@FXML private ProgressIndicator piCheckingFingerprints;
	@FXML private ImageView ivGeneratingNewCriminalBiometricsIdSuccess;
	@FXML private ImageView ivGeneratingNewCriminalBiometricsIdFailure;
	@FXML private ImageView ivSubmittingFingerprintsSuccess;
	@FXML private ImageView ivSubmittingFingerprintsFailure;
	@FXML private ImageView ivCheckingFingerprintsSuccess;
	@FXML private ImageView ivCheckingFingerprintsFailure;
	@FXML private Label lblGeneratingNewCriminalBiometricsId;
	@FXML private Label lblSubmittingFingerprints;
	@FXML private Label lblCheckingFingerprints;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		request = Request.GENERATE_NEW_CRIMINAL_BIOMETRICS_ID;
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
				txtCriminalBiometricsId.setText(String.valueOf(criminalBiometricsId));
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
					btnStartOver.setVisible(true);
					showSuccessNotification(resources.getString("registerCriminalFingerprints.success.message"));
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
	}
	
	@Override
	public void reportNegativeTaskResponse(String errorCode, Throwable exception, String[] errorDetails)
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
		
		btnStartOver.setVisible(false);
		btnRetry.setVisible(false);
		
		continueWorkflow();
	}
}