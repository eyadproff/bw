package sa.gov.nic.bio.bw.client.features.cancelcriminal.workflow;

import javafx.application.Platform;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.CancelCriminalPaneFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CancelCriminalWorkflow extends WorkflowBase<Void, Void>
{
	public CancelCriminalWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> workflowResponse = new HashMap<>();
		Platform.runLater(() -> Context.getCoreFxController().clearWizardBar());
		
		while(true)
		{
			formRenderer.get().renderForm(CancelCriminalPaneFxController.class, workflowResponse);
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			Long personId = (Long) userTaskDataMap.get("personId");
			Long inquiryId = (Long) userTaskDataMap.get("inquiryId");
			Integer samisIdTypes = (Integer) userTaskDataMap.get("samisIdTypes");
			Long criminalId = (Long) userTaskDataMap.get("criminalId");
			
			ServiceResponse<Boolean> response;
			
			if(personId != null) // by person id
			{
				response = CancelCriminalService.execute(personId, samisIdTypes, criminalId);
			}
			else // by inquiry id
			{
				response = CancelCriminalService.execute(inquiryId, criminalId);
			}
			
			workflowResponse.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}