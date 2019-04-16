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

@FxmlFile("civilBiometricsId.fxml")
public class CivilBiometricsIdPaneFxController extends WizardStepFxControllerBase
{
	@Output private Long civilBiometricsId;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtCivilBiometricsId;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtCivilBiometricsId, "\\d*", "[^\\d]",
		                                   10);
		if(civilBiometricsId != null) txtCivilBiometricsId.setText(String.valueOf(civilBiometricsId));
		
		btnPrevious.disableProperty().bind(txtCivilBiometricsId.disabledProperty());
		btnNext.disableProperty().bind(txtCivilBiometricsId.textProperty().isEmpty().or(
																			txtCivilBiometricsId.disabledProperty()));
		txtCivilBiometricsId.requestFocus();
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
		txtCivilBiometricsId.setDisable(bShow);
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		if(!btnNext.isDisabled()) civilBiometricsId = Long.parseLong(txtCivilBiometricsId.getText());
	}
	
	@Override
	protected void onNextButtonClicked(ActionEvent actionEvent)
	{
		if(btnNext.isDisabled()) return;
		
		civilBiometricsId = Long.parseLong(txtCivilBiometricsId.getText());
		continueWorkflow();
	}
}