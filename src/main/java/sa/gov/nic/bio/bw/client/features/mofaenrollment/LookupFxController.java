package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.HashMap;

public class LookupFxController extends WizardStepFxControllerBase
{
	@FXML private ProgressIndicator progressIndicator;
	@FXML private Button btnTryAgain;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/lookup.fxml");
	}
	
	@FXML
	private void initialize(){}
	
	@Override
	public void onControllerReady()
	{
		submitTask();
	}
	
	@Override
	public void onReturnFromTask() // return only in case of errors
	{
		GuiUtils.showNode(progressIndicator, false);
		GuiUtils.showNode(btnTryAgain, true);
		
		super.onReturnFromTask(); // show the error message on the notification pane
	}
	
	@FXML
	private void onTryAgainButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(progressIndicator, true);
		GuiUtils.showNode(btnTryAgain, false);
		hideNotification();
		
		submitTask();
	}
	
	private void submitTask()
	{
		coreFxController.submitFormTask(new HashMap<>());
	}
}