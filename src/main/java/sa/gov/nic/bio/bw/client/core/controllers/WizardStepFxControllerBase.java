package sa.gov.nic.bio.bw.client.core.controllers;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.NavigableController;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;

import java.util.HashMap;
import java.util.Map;

public abstract class WizardStepFxControllerBase extends BodyFxControllerBase implements NavigableController
{
	@Override
	public void goNext()
	{
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
		hideNotification();
		Context.getCoreFxController().showWizardTransitionProgressIndicator(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(WizardWorkflowBase.KEY_WORKFLOW_DIRECTION,
		              WizardWorkflowBase.VALUE_WORKFLOW_DIRECTION_START_OVER);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	protected void onGoingNext(Map<String, Object> uiDataMap){}
	protected void onGoingPrevious(Map<String, Object> uiDataMap){}
}