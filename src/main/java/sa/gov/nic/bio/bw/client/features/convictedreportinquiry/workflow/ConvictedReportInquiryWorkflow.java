package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.workflow;

import javafx.application.Platform;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.ConvictedReportInquiryPaneFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ConvictedReportInquiryWorkflow extends WorkflowBase<Void, Void>
{
	public ConvictedReportInquiryWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
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
			formRenderer.get().renderForm(ConvictedReportInquiryPaneFxController.class, workflowResponse);
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			Long personId = (Long) userTaskDataMap.get("personId");
			Long inquiryId = (Long) userTaskDataMap.get("inquiryId");
			Integer personIdType = (Integer) userTaskDataMap.get("personIdType");
			Long criminalId = (Long) userTaskDataMap.get("criminalId");
			
			ServiceResponse<Boolean> response;
			
			if(personId != null) // by person id
			{
				response = ConvictedReportInquiryService.execute(personId, personIdType, criminalId);
			}
			else // by inquiry id
			{
				response = ConvictedReportInquiryService.execute(inquiryId, criminalId);
			}
			
			workflowResponse.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}