package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow.SearchByFaceImageWorkflow;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFxController extends WizardStepFxControllerBase
{
	@FXML private ProgressIndicator piProgress;
	@FXML private Label txtProgress;
	@FXML private Button btnRetry;
	@FXML private Button btnStartOver;
	
	private List<Candidate> candidates;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/search.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnRetry);
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		ServiceResponse<List<Candidate>> response = (ServiceResponse<List<Candidate>>)
																	uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
		
		if(response == null) // first call, show a progress
		{
			GuiUtils.showNode(btnRetry, false);
			GuiUtils.showNode(piProgress, true);
			GuiUtils.showNode(txtProgress, true);
			btnStartOver.setDisable(true);
		}
		else // there is a result
		{
			if(response.isSuccess())
			{
				candidates = response.getResult();
				goNext();
			}
			else
			{
				GuiUtils.showNode(piProgress, false);
				GuiUtils.showNode(txtProgress, false);
				GuiUtils.showNode(btnRetry, true);
				btnStartOver.setDisable(false);
				
				reportNegativeResponse(response.getErrorCode(), response.getException(), response.getErrorDetails());
			}
		}
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		uiDataMap.put(SearchByFaceImageWorkflow.KEY_CANDIDATES, candidates);
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(Workflow.KEY_WEBSERVICE_RESPONSE, null);
		coreFxController.submitForm(uiDataMap);
	}
}