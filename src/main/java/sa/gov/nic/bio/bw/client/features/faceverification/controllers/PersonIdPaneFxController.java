package sa.gov.nic.bio.bw.client.features.faceverification.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Output;

import java.util.Map;

@FxmlFile("personId.fxml")
public class PersonIdPaneFxController extends WizardStepFxControllerBase
{
	@Output private Long personId;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtPersonId;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		
		btnNext.disableProperty().bind(txtPersonId.textProperty().isEmpty().or(txtPersonId.disabledProperty()));
		btnNext.setOnAction(actionEvent -> goNext());
		
		if(personId != null) txtPersonId.setText(String.valueOf(personId));
		
		txtPersonId.requestFocus();
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		personId = Long.valueOf(txtPersonId.getText());
	}
	
	@FXML
	private void onEnterPressed(ActionEvent actionEvent)
	{
		btnNext.fire();
	}
}