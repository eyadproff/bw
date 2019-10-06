package sa.gov.nic.bio.bw.workflow.registeriris.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.registeriris.tasks.CheckIrisRegistrationWorkflowTask.Status;

@FxmlFile("registeringIris.fxml")
public class RegisteringIrisPaneFxController extends WizardStepFxControllerBase
{
	public enum Request
	{
		SUBMIT_IRIS_REGISTRATION,
		CHECK_IRIS_REGISTRATION
	}
	
	@Input protected Long personId;
	@Input protected String rightIrisBase64;
	@Input protected String leftIrisBase64;
	@Input private Status irisRegistrationStatus;
	@Output	private Request request;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private ImageView ivSuccess;
	@FXML private ImageView ivFailure;
	@FXML private Label lblStatus;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	private boolean disableRetryButtonForever = false;
	
	@Override
	protected void onAttachedToScene()
	{
		request = Request.SUBMIT_IRIS_REGISTRATION;
		continueWorkflow();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(request == Request.SUBMIT_IRIS_REGISTRATION)
		{
			if(successfulResponse)
			{
				lblStatus.setText(resources.getString("label.waitingIrisRegistration"));
				request = Request.CHECK_IRIS_REGISTRATION;
				continueWorkflow();
			}
			else
			{
				lblStatus.setText(resources.getString("label.failedToSendIris"));
				piProgress.setVisible(false);
				ivFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(!disableRetryButtonForever);
			}
		}
		else if(request == Request.CHECK_IRIS_REGISTRATION)
		{
			if(successfulResponse)
			{
				if(irisRegistrationStatus == Status.PENDING)
				{
					Context.getExecutorService().submit(() ->
					{
						try
						{
							int seconds = Integer.parseInt(Context.getConfigManager().getProperty(
																	"registerIris.inquiry.checkEverySeconds"));
							Thread.sleep(seconds * 1000);
						}
						catch(InterruptedException e)
						{
							e.printStackTrace();
						}
						
						Platform.runLater(this::continueWorkflow);
					});
					
				}
				else if(irisRegistrationStatus == Status.SUCCESS)
				{
					lblStatus.setText(resources.getString("label.successIrisRegistration"));
					piProgress.setVisible(false);
					ivSuccess.setVisible(true);
					btnStartOver.setVisible(true);
				}
			}
			else
			{
				lblStatus.setText(resources.getString("label.failedToRegisterIris"));
				piProgress.setVisible(false);
				ivFailure.setVisible(true);
				btnStartOver.setVisible(true);
				btnRetry.setVisible(!disableRetryButtonForever);
			}
		}
	}
	
	@Override
	public void reportNegativeTaskResponse(String errorCode, Throwable exception, String[] errorDetails)
	{
		if("B003-0066".equals(errorCode)) disableRetryButtonForever = true;
		
		super.reportNegativeTaskResponse(errorCode, exception, errorDetails);
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		btnRetry.setVisible(false);
		btnStartOver.setVisible(false);
		ivFailure.setVisible(false);
		piProgress.setVisible(true);
		
		if(request == Request.SUBMIT_IRIS_REGISTRATION)
		{
			lblStatus.setText(resources.getString("label.submittingIris"));
		}
		else if(request == Request.CHECK_IRIS_REGISTRATION)
		{
			lblStatus.setText(resources.getString("label.waitingIrisRegistration"));
		}
		
		continueWorkflow();
	}
}