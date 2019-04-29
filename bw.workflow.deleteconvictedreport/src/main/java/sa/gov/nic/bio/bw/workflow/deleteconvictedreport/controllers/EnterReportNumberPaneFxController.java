package sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.deleteconvictedreport.utils.DeleteConvictedReportErrorCodes;

@FxmlFile("enterReportNumber.fxml")
public class EnterReportNumberPaneFxController extends WizardStepFxControllerBase
{
	@Input private ConvictedReport convictedReport;
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
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		
		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			Integer status = convictedReport.getStatus();
			
			if(status.equals(ConvictedReport.Status.ACTIVE)) goNext();
			else if(status.equals(ConvictedReport.Status.UPDATED))
			{
				showWarningNotification(resources.getString("getConvictedReport.failure.updated"));
			}
			else if(status.equals(ConvictedReport.Status.DELETED))
			{
				showWarningNotification(resources.getString("getConvictedReport.failure.deleted"));
			}
			else
			{
				String errorCode = DeleteConvictedReportErrorCodes.C014_00001.getCode();
				String[] errorDetails = {"The returned convicted report has no status!"};
				Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
			}
		}
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