package sa.gov.nic.bio.bw.client.core.workflow;

import java.util.Map;

public interface Workflow<I, O>
{
	String KEY_WEBSERVICE_RESPONSE = "WEBSERVICE_RESPONSE";
	
	void submitUserTask(Map<String, Object> uiDataMap) throws InterruptedException;
	O onProcess(I input) throws InterruptedException, Signal;
}