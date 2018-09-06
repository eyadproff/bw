package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LookupFxController extends WizardStepFxControllerBase
{
	@FXML private ProgressIndicator progressIndicator;
	@FXML private Button btnTryAgain;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/lookup.fxml");
	}
	
	@Override
	protected void initialize(){}
	
	@Override
	protected void onAttachedToScene()
	{
		submitTask();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> dataMap)
	{
		if(!newForm)
		{
			GuiUtils.showNode(progressIndicator, false);
			GuiUtils.showNode(btnTryAgain, true);
			
			ServiceResponse<?> serviceResponse = (ServiceResponse<?>) dataMap.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			if(!serviceResponse.isSuccess()) reportNegativeResponse(serviceResponse.getErrorCode(),
			                                                        serviceResponse.getException(),
			                                                        serviceResponse.getErrorDetails());
		}
	}
	
	@FXML
	private void onTryAgainButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(progressIndicator, true);
		GuiUtils.showNode(btnTryAgain, false);
		hideNotification();
		
		submitTask();
	}
	
	private void submitTask()
	{
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(new HashMap<>());
	}
}