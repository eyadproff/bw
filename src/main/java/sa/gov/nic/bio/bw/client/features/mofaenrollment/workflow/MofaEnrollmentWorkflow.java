package sa.gov.nic.bio.bw.client.features.mofaenrollment.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancellatent.workflow.CancelLatentService;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.LookupFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MofaEnrollmentWorkflow extends WorkflowBase<Void, Void>
{
	public MofaEnrollmentWorkflow(AtomicReference<FormRenderer> formRenderer,
	                              BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> uiInputData = new HashMap<>();
		
		while(true)
		{
			formRenderer.get().renderForm(LookupFxController.class, uiInputData);
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			Long personId = (Long) userTaskDataMap.get("personId");
			String latentId = (String) userTaskDataMap.get("latentId");
			
			ServiceResponse<Boolean> response = CancelLatentService.execute(personId, latentId);
			
			uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}