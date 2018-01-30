package sa.gov.nic.bio.bw.client.core.interfaces;

import java.util.Map;

public interface FormRenderer
{
	void renderForm(Class<?> controllerClass, Map<String, Object> inputDataMap);
}