package sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.Map;

@FxmlFile("criminalBiometricsId.fxml")
public class CriminalBiometricsIdPaneFxController extends WizardStepFxControllerBase
{
	@Output private Long criminalBiometricsId;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtCriminalBiometricsId, "\\d*", "[^\\d]",
		                                   10);
		if(criminalBiometricsId != null) txtCriminalBiometricsId.setText(String.valueOf(criminalBiometricsId));
		
		btnPrevious.disableProperty().bind(txtCriminalBiometricsId.disabledProperty());
		btnNext.disableProperty().bind(txtCriminalBiometricsId.textProperty().isEmpty().or(
				txtCriminalBiometricsId.disabledProperty()));
		txtCriminalBiometricsId.requestFocus();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse) goNext();
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		piProgress.setVisible(bShow);
		txtCriminalBiometricsId.setDisable(bShow);
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		if(!btnNext.isDisabled()) criminalBiometricsId = Long.parseLong(txtCriminalBiometricsId.getText());
	}
	
	@Override
	protected void onNextButtonClicked(ActionEvent actionEvent)
	{
		if(btnNext.isDisabled()) return;
		
		criminalBiometricsId = Long.parseLong(txtCriminalBiometricsId.getText());
		continueWorkflow();
	}
}