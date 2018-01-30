package sa.gov.nic.bio.bw.client.core.interfaces;

import java.util.Map;

public interface FormTaskController
{
	void onReturnFromServiceTask(boolean firstVisit, Map<String, Object> dataMap);
}