package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.controllers;

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

@FxmlFile("fetchingPersonInfo.fxml")
public class FetchingPersonInfoPaneFxController extends WizardStepFxControllerBase
{
	@FXML private VBox paneError;
	@FXML private VBox paneDevicesRunnerNotRunning;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblProgress;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
		{
		    GuiUtils.showNode(piProgress, running);
		    GuiUtils.showNode(lblProgress, running);
		    GuiUtils.showNode(paneDevicesRunnerNotRunning, !running);
		    GuiUtils.showNode(btnStartOver, !running);
		
		    continueWorkflow();
		});
		
		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(lblProgress, false);
			GuiUtils.showNode(paneError, false);
			GuiUtils.showNode(paneDevicesRunnerNotRunning, true);
			GuiUtils.showNode(btnStartOver, true);
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
		else continueWorkflow();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse) goNext();
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		GuiUtils.showNode(paneError, !bShow);
		GuiUtils.showNode(btnRetry, !bShow);
		GuiUtils.showNode(btnStartOver, !bShow);
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(lblProgress, bShow);
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		continueWorkflow();
	}
}