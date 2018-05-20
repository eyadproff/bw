package sa.gov.nic.bio.bw.client.features.foreignenrollment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class ShowReceiptFxController extends WizardStepFxControllerBase
{
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/showReceipt.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	protected void onAttachedToScene()
	{
	
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
	
	}
}