package sa.gov.nic.bio.bw.client.features.cancelcriminal;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.client.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.controllers.CancelCriminalPaneFxController;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.workflow.CancelCriminalService;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.cancel.cancelCriminal", title = "menu.title", order = 1)
@WithLookups(SamisIdTypesLookup.class)
public class CancelCriminalWorkflow extends SinglePageWorkflowBase
{
	public CancelCriminalWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public boolean onStep() throws InterruptedException, Signal
	{
		renderUi(CancelCriminalPaneFxController.class);
		waitForUserInput();
		
		Long personId = (Long) uiInputData.get("personId");
		Long inquiryId = (Long) uiInputData.get("inquiryId");
		Integer samisIdType = (Integer) uiInputData.get("samisIdType");
		Long criminalId = (Long) uiInputData.get("criminalId");
		uiInputData.clear();
		
		ServiceResponse<Boolean> response;
		
		if(personId != null) // by person id
		{
			response = CancelCriminalService.execute(personId, samisIdType, criminalId);
		}
		else // by inquiry id
		{
			response = CancelCriminalService.execute(inquiryId, criminalId);
		}
		
		uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
		
		return true;
	}
}