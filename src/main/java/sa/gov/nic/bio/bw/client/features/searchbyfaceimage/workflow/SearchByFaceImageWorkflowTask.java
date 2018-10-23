package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.SearchByFaceImageAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class SearchByFaceImageWorkflowTask implements WorkflowTask
{
	@Input(required = true) private String faceImageBase64;
	@Output private List<Candidate> candidates;
	
	@Override
	public void execute() throws Signal
	{
		SearchByFaceImageAPI searchByFaceImageAPI = Context.getWebserviceManager().getApi(SearchByFaceImageAPI.class);
		Call<List<Candidate>> apiCall = searchByFaceImageAPI.searchByFaceImage(faceImageBase64);
		TaskResponse<List<Candidate>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		candidates = taskResponse.getResult();
	}
}