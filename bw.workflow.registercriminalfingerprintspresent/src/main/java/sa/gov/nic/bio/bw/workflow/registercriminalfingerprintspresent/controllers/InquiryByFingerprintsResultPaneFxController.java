package sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;

@FxmlFile("inquiryByFingerprintsResult.fxml")
public class InquiryByFingerprintsResultPaneFxController extends WizardStepFxControllerBase
{
	@Input protected Long criminalBiometricsId;
	
	@FXML private Pane panePass;
	@FXML private Pane paneNoPass;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterFingerprints;
	
	@Override
	protected void onAttachedToScene()
	{
		if(criminalBiometricsId != null && criminalBiometricsId > 0L) // criminal hit
		{
			GuiUtils.showNode(paneNoPass, true);
			txtCriminalBiometricsId.setText(String.valueOf(criminalBiometricsId));
		}
		else // no criminal hit
		{
			GuiUtils.showNode(panePass, true);
			GuiUtils.showNode(btnRegisterFingerprints, true);
		}
	}
	
	@FXML
	private void onRegisterFingerprintsButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("registerCriminalFingerprints.confirmation.header");
		String contentText = resources.getString("registerCriminalFingerprints.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) goNext();
	}
}