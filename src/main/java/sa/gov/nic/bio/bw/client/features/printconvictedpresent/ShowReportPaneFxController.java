package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class ShowReportPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_CONVICTED_REPORT_NUMBER = "CONVICTED_REPORT_NUMBER";
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/showReport.fxml");
	}
	
	@Override
	protected void initialize()
	{
	
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
	
	}
}