package sa.gov.nic.bio.bw.client.features.cancellatent.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancellatent.CancelLatentPaneFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class CancelLatentWorkflow extends WorkflowBase<Void, Void>
{
	public CancelLatentWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> uiInputData = new HashMap<>();
		
		while(true)
		{
			formRenderer.renderForm(CancelLatentPaneFxController.class, uiInputData);
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			String personId = (String) userTaskDataMap.get("personId");
			String latentId = (String) userTaskDataMap.get("latentId");
			
			ServiceResponse<Boolean> response = CancelLatentService.execute(personId, latentId);
			
			uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}