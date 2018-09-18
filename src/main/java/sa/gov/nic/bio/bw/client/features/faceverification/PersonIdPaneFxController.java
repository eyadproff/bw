package sa.gov.nic.bio.bw.client.features.faceverification;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Output;

import java.net.URL;
import java.util.Map;

public class PersonIdPaneFxController extends WizardStepFxControllerBase
{
	@Output private Long personId;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtPersonId;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/personId.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		
		btnNext.disableProperty().bind(txtPersonId.textProperty().isEmpty().or(txtPersonId.disabledProperty()));
		btnNext.setOnAction(actionEvent -> goNext());
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			if(personId != null) txtPersonId.setText(String.valueOf(personId));
			
			txtPersonId.requestFocus();
		}
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