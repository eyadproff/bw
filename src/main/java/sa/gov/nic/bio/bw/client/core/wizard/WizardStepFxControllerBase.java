package sa.gov.nic.bio.bw.client.core.wizard;

import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.NavigableController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class WizardStepFxControllerBase extends BodyFxControllerBase implements NavigableController
{
	@Override
	public void goNext()
	{
		Context.getCoreFxController().showWizardTransitionProgressIndicator(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "forward");
		onGoingNext(uiDataMap);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	@Override
	public void goPrevious()
	{
		Context.getCoreFxController().showWizardTransitionProgressIndicator(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "backward");
		onGoingPrevious(uiDataMap);
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	@Override
	public void startOver()
	{
		Context.getCoreFxController().showWizardTransitionProgressIndicator(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "startOver");
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	protected void onGoingNext(Map<String, Object> uiDataMap){}
	protected void onGoingPrevious(Map<String, Object> uiDataMap){}
	public abstract URL getFxmlLocation();
}