package sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;

@FxmlFile("showResult.fxml")
public class ShowResultPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private long oldReportNumber;
	@Input(alwaysRequired = true) private long newReportNumber;
	
	@FXML private TextField txtOldReportNumber;
	@FXML private TextField txtNewReportNumber;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		txtOldReportNumber.setText(String.valueOf(oldReportNumber));
		txtNewReportNumber.setText(String.valueOf(newReportNumber));
		btnStartOver.requestFocus();
	}
}