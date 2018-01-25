package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;

public class DoneFxController extends WizardStepFxControllerBase
{
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/done.fxml");
	}
	
	@FXML
	private void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	public void onControllerReady()
	{
	
	}
}