package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.SearchByFaceImagePaneFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class SearchByFaceImageWorkflow extends WorkflowBase<Void, Void>
{
	public SearchByFaceImageWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> uiInputData = new HashMap<>();
		
		while(true)
		{
			formRenderer.get().renderForm(SearchByFaceImagePaneFxController.class, uiInputData);
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			String uploadedImagePath = (String) userTaskDataMap.get("uploadedImagePath");
			ServiceResponse<List<Candidate>> response = SearchByFaceImageService.execute(uploadedImagePath);
			uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}