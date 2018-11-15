package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.HashMap;

@FxmlFile("recordId.fxml")
public class RecordIdPaneFxController extends WizardStepFxControllerBase
{
	@Output private Long recordId;
	
	@FXML private TextField txtRecordId;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtRecordId, "\\d*", "[^\\d]", 15);
		
		btnNext.disableProperty().bind(txtRecordId.textProperty().isEmpty().or(txtRecordId.disabledProperty()));
		btnNext.setOnAction(actionEvent ->
		{
		    hideNotification();
			txtRecordId.setDisable(true);
			piProgress.setVisible(true);
			
			recordId = Long.parseLong(txtRecordId.getText());
			if(!isDetached()) Context.getWorkflowManager().submitUserTask(new HashMap<>());
		});
		
		if(recordId != null) txtRecordId.setText(String.valueOf(recordId));
		txtRecordId.requestFocus();
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
		txtRecordId.setDisable(bShow);
	}
	
	@FXML
	private void onEnterPressed(ActionEvent actionEvent)
	{
		btnNext.fire();
	}
}