package sa.gov.nic.bio.bw.client.core.wizard;

import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.interfaces.NavigableController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class WizardStepFxControllerBase extends BodyFxControllerBase implements NavigableController
{
	public URL getWizardFxmlLocation()
	{
		return getClass().getResource("fxml/wizard.fxml");
	}
	
	@Override
	public void goNext()
	{
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "forward");
		onGoingNext(uiDataMap);
		coreFxController.submitFormTask(uiDataMap);
	}
	
	@Override
	public void goPrevious()
	{
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "backward");
		coreFxController.submitFormTask(uiDataMap);
	}
	
	@Override
	public void startOver()
	{
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "startOver");
		coreFxController.submitFormTask(uiDataMap);
	}
	
	protected void onGoingNext(Map<String, Object> uiDataMap){}
	protected void onGoingPrevious(Map<String, Object> uiDataMap){}
	public abstract URL getFxmlLocation();
}