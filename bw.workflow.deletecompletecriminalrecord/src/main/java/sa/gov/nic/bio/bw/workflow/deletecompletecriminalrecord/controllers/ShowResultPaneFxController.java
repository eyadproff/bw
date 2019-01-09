package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;

@FxmlFile("showResult.fxml")
public class ShowResultPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private long criminalBiometricsId;
	
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		txtCriminalBiometricsId.setText(String.valueOf(criminalBiometricsId));
		btnStartOver.requestFocus();
	}
}