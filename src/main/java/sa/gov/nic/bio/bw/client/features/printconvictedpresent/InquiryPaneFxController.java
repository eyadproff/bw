package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class InquiryPaneFxController extends WizardStepFxControllerBase
{
	public ProgressIndicator piProgress;
	public Label txtProgress;
	public Button btnRetry;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/inquiry.fxml");
	}
	
	@Override
	protected void initialize()
	{
	
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
		
		}
		else // return from
		{
		
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
	
	}
}