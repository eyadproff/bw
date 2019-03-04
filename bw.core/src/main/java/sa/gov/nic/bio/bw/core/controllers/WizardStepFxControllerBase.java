package sa.gov.nic.bio.bw.core.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.NavigableController;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;

import java.util.HashMap;
import java.util.Map;

public abstract class WizardStepFxControllerBase extends BodyFxControllerBase implements NavigableController
{
	@Output
	protected Object state;
	
	@Override
	public void goNext()
	{
		state = new Object();
		
		hideNotification();
		Context.getCoreFxController().showWizardTransitionProgressIndicator(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(WizardWorkflowBase.KEY_WORKFLOW_DIRECTION, WizardWorkflowBase.VALUE_WORKFLOW_DIRECTION_FORWARD);
		onGoingNext(uiDataMap);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	@Override
	public void goPrevious()
	{
		state = new Object();
		
		hideNotification();
		Context.getCoreFxController().showWizardTransitionProgressIndicator(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(WizardWorkflowBase.KEY_WORKFLOW_DIRECTION, WizardWorkflowBase.VALUE_WORKFLOW_DIRECTION_BACKWARD);
		onGoingPrevious(uiDataMap);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	@Override
	public void startOver()
	{
		state = null;
		
		hideNotification();
		Context.getCoreFxController().showWizardTransitionProgressIndicator(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(WizardWorkflowBase.KEY_WORKFLOW_DIRECTION,
		              WizardWorkflowBase.VALUE_WORKFLOW_DIRECTION_START_OVER);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	protected void onGoingNext(Map<String, Object> uiDataMap){}
	protected void onGoingPrevious(Map<String, Object> uiDataMap){}
	
	@FXML
	protected void onPreviousButtonClicked(ActionEvent actionEvent)
	{
		goPrevious();
	}
	
	@FXML
	protected void onNextButtonClicked(ActionEvent actionEvent)
	{
		goNext();
	}
	
	@FXML
	protected void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		String headerText = Context.getCoreFxController().getResourceBundle()
														 .getString("startingOver.confirmation.header");
		String contentText = Context.getCoreFxController().getResourceBundle()
														  .getString("startingOver.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) startOver();
	}
	
	public boolean isFirstLoad()
	{
		return state == null;
	}
}