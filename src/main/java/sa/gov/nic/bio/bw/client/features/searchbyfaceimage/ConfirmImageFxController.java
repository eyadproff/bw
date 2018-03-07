package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class ConfirmImageFxController extends WizardStepFxControllerBase
{
	@FXML private Button btnPrevious;
	@FXML private Button btnSearch;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/confirmImage.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(event -> goPrevious());
		btnSearch.setOnAction(event -> goNext());
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
	
	}
}