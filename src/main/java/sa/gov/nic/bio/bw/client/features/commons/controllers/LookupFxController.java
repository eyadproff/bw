package sa.gov.nic.bio.bw.client.features.commons.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

@FxmlFile("lookup.fxml")
public class LookupFxController extends WizardStepFxControllerBase
{
	@FXML private ProgressIndicator progressIndicator;
	@FXML private Button btnTryAgain;
	
	@Override
	protected void onAttachedToScene()
	{
		continueWorkflow();
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		GuiUtils.showNode(progressIndicator, bShow);
		GuiUtils.showNode(btnTryAgain, !bShow);
	}
	
	@FXML
	private void onTryAgainButtonClicked(ActionEvent actionEvent)
	{
		continueWorkflow();
	}
}