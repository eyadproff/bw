package sa.gov.nic.bio.bw.client.features.cancelcriminal.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.CancelCriminalPaneFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class CancelCriminalWorkflow extends WorkflowBase<Void, Void>
{
	public CancelCriminalWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> workflowResponse = new HashMap<>();
		
		while(true)
		{
			formRenderer.renderForm(CancelCriminalPaneFxController.class, workflowResponse);
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			String personId = (String) userTaskDataMap.get("personId");
			String criminalId = (String) userTaskDataMap.get("criminalId");
			
			ServiceResponse<Boolean> response = CancelCriminalService.execute(personId, criminalId);
			
			workflowResponse.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}