package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers;

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

@FxmlFile("miscreantId.fxml")
public class MiscreantIdPaneFxController extends WizardStepFxControllerBase
{
	@Output private Integer miscreantId;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtPersonId;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 9);
		if(miscreantId != null) txtPersonId.setText(String.valueOf(miscreantId));
		
		btnNext.disableProperty().bind(txtPersonId.textProperty().isEmpty().or(txtPersonId.disabledProperty()));
		txtPersonId.requestFocus();
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
		txtPersonId.setDisable(bShow);
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		if(!btnNext.isDisabled()) miscreantId = Integer.parseInt(txtPersonId.getText());
	}
	
	@Override
	protected void onNextButtonClicked(ActionEvent actionEvent)
	{
		if(btnNext.isDisabled()) return;
		
		miscreantId = Integer.parseInt(txtPersonId.getText());
		continueWorkflow();
	}
}