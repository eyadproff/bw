package sa.gov.nic.bio.bw.client.features.faceverification.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

@FxmlFile("matching.fxml")
public class MatchingFxController extends WizardStepFxControllerBase
{
	@FXML private ProgressIndicator piProgress;
	@FXML private Label txtProgress;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		btnStartOver.setOnAction(event -> startOver());
		continueWorkflow();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse) goNext();
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		GuiUtils.showNode(btnRetry, !bShow);
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(txtProgress, bShow);
		btnStartOver.setDisable(bShow);
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		continueWorkflow();
	}
}