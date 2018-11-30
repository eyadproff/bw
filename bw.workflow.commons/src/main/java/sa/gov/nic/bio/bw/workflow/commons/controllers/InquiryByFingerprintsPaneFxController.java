package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;

@FxmlFile("inquiryByFingerprints.fxml")
public class InquiryByFingerprintsPaneFxController extends WizardStepFxControllerBase
{
	@Input(requiredOnReturn = true) private Status status;
	
	@FXML private VBox paneError;
	@FXML private VBox paneDevicesRunnerNotRunning;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblProgress;
	@FXML private Label lblCanceling;
	@FXML private Label lblCancelled;
	@FXML private Button btnCancel;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	private boolean fingerprintInquiryCancelled = false;
	
	@Override
	protected void onAttachedToScene()
	{
		btnCancel.setOnAction(actionEvent ->
		{
		    GuiUtils.showNode(lblProgress, false);
		    GuiUtils.showNode(btnCancel, false);
		    GuiUtils.showNode(lblCanceling, true);
		    fingerprintInquiryCancelled = true;
		});
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
		{
		    GuiUtils.showNode(piProgress, running);
		    GuiUtils.showNode(lblProgress, running);
		    GuiUtils.showNode(btnCancel, running);
		    GuiUtils.showNode(paneDevicesRunnerNotRunning, !running);
		    GuiUtils.showNode(btnStartOver, !running);
		
		    if(running)
		    {
			    showProgress(true);
		    	continueWorkflow();
		    }
		});
		
		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			showProgress(false);
			GuiUtils.showNode(paneDevicesRunnerNotRunning, true);
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
		else
		{
			showProgress(true);
			continueWorkflow();
		}
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			if(status == Status.PENDING)
			{
				btnCancel.setDisable(false);
				Context.getExecutorService().submit(() ->
				{
				    int seconds = Integer.parseInt(
				                    Context.getConfigManager().getProperty("fingerprint.inquiry.checkEverySeconds"));
				
				    try
				    {
				        Thread.sleep(seconds * 1000);
				    }
				    catch(InterruptedException e)
				    {
				        e.printStackTrace();
				    }
				
				    Platform.runLater(() ->
				    {
				        if(fingerprintInquiryCancelled)
				        {
				            fingerprintInquiryCancelled = false;
				            GuiUtils.showNode(piProgress, false);
				            GuiUtils.showNode(lblCanceling, false);
				            GuiUtils.showNode(lblCancelled, true);
				            GuiUtils.showNode(btnStartOver, true);
				            GuiUtils.showNode(btnRetry, true);
				        }
				        else continueWorkflow();
				    });
				});
			}
			else goNext();
		}
		else showProgress(false);
	}
	
	private void showProgress(boolean bShow)
	{
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(lblProgress, bShow);
		GuiUtils.showNode(btnCancel, bShow);
		GuiUtils.showNode(btnRetry, !bShow);
		GuiUtils.showNode(btnStartOver, !bShow);
		GuiUtils.showNode(paneError, !bShow);
		GuiUtils.showNode(lblCanceling, false);
		GuiUtils.showNode(lblCancelled, false);
	}
	
	@Override
	protected void onDetachingFromScene()
	{
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(null);
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		showProgress(true);
		continueWorkflow();
	}
}