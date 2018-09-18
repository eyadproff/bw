package sa.gov.nic.bio.bw.client.features.searchbyfaceimage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
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
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/search.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			showProgress(true);
			continueWorkflow();
		}
		else
		{
			ServiceResponse<List<Candidate>> response = (ServiceResponse<List<Candidate>>)
																	uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			if(response != null) // there is a result
			{
				if(response.isSuccess()) goNext();
				else
				{
					showProgress(false);
					reportNegativeResponse(response.getErrorCode(), response.getException(),
					                       response.getErrorDetails());
				}
			}
		}
	}
	
	@FXML
	private void onRetryButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		showProgress(true);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(Workflow.KEY_WEBSERVICE_RESPONSE, null);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	private void showProgress(boolean bShow)
	{
		GuiUtils.showNode(btnRetry, !bShow);
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(txtProgress, bShow);
		btnStartOver.setDisable(bShow);
	}
}