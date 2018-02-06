package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class DoneFxController extends WizardStepFxControllerBase
{
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/done.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	public void onControllerReady()
	{
	
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> dataMap)
	{
	
	}
}