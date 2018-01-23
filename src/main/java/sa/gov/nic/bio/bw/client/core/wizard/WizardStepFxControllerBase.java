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
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "forward");
		onGoingNext(uiDataMap);
		coreFxController.submitFormTask(uiDataMap);
	}
	
	@Override
	public void goPrevious()
	{
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "backward");
		coreFxController.submitFormTask(uiDataMap);
	}
	
	@Override
	public void startOver()
	{
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "startOver");
		coreFxController.submitFormTask(uiDataMap);
	}
	
	protected void onGoingNext(Map<String, String> uiDataMap){}
	protected void onGoingPrevious(Map<String, String> uiDataMap){}
	public abstract URL getFxmlLocation();
}