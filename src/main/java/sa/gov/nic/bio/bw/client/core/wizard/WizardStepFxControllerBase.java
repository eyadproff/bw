package sa.gov.nic.bio.bw.client.core.wizard;

import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
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
		Context.getCoreFxController().showBodyOverlayPane(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "forward");
		onGoingNext(uiDataMap);
		onLeaving(uiDataMap);
		Context.getCoreFxController().submitForm(uiDataMap);
	}
	
	@Override
	public void goPrevious()
	{
		Context.getCoreFxController().showBodyOverlayPane(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "backward");
		onGoingPrevious(uiDataMap);
		onLeaving(uiDataMap);
		Context.getCoreFxController().submitForm(uiDataMap);
	}
	
	@Override
	public void startOver()
	{
		Context.getCoreFxController().showBodyOverlayPane(true);
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put("direction", "startOver");
		onLeaving(uiDataMap);
		Context.getCoreFxController().submitForm(uiDataMap);
	}
	
	protected void onGoingNext(Map<String, Object> uiDataMap){}
	protected void onGoingPrevious(Map<String, Object> uiDataMap){}
	protected void onLeaving(Map<String, Object> uiDataMap){}
	public abstract URL getFxmlLocation();
}