package sa.gov.nic.bio.bw.client.features.citizenenrollment.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Output;

@FxmlFile("personId.fxml")
public class PersonIdPaneFxController extends WizardStepFxControllerBase
{
	@Output private long personId;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtPersonId;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtPersonId, "^1\\d*", "\\D+|^[02-9]",
		                                   10);
		
		btnNext.disableProperty().bind(txtPersonId.textProperty().isEmpty().or(txtPersonId.disabledProperty()));
		btnNext.setOnAction(actionEvent ->
		{
			piProgress.setVisible(true);
			txtPersonId.setDisable(true);
			
			personId = Long.parseLong(txtPersonId.getText());
			continueWorkflow();
		});
		
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
	
	@FXML
	private void onEnterPressed(ActionEvent actionEvent)
	{
		btnNext.fire();
	}
}