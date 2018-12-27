package sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Output;

@FxmlFile("enterReportNumber.fxml")
public class EnterReportNumberPaneFxController extends WizardStepFxControllerBase
{
	@Output private Long reportNumber;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtReportNumber;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtReportNumber, "\\d*", "[^\\d]",
		                                   18);
		
		btnNext.disableProperty().bind(txtReportNumber.textProperty().isEmpty().or(txtReportNumber.disabledProperty()));
		txtReportNumber.requestFocus();
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
		txtReportNumber.setDisable(bShow);
	}
	
	@Override
	protected void onNextButtonClicked(ActionEvent actionEvent)
	{
		if(btnNext.isDisabled()) return;
		
		reportNumber = Long.parseLong(txtReportNumber.getText());
		continueWorkflow();
	}
}