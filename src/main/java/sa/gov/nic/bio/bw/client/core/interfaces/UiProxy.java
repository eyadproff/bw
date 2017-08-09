package sa.gov.nic.bio.bw.client.core.interfaces;

import java.util.Map;

public interface UiProxy
{
	void showForm(String formKey, String taskId, Map<String, Object> inputData);
}