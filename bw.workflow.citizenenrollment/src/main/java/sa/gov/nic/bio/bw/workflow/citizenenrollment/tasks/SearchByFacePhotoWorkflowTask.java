package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.beans.Candidate;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice.SearchByFaceImageAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class SearchByFacePhotoWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private String facePhotoBase64;
	@Output private List<Candidate> candidates;
	
	@Override
	public void execute() throws Signal
	{
		SearchByFaceImageAPI searchByFaceImageAPI = Context.getWebserviceManager().getApi(SearchByFaceImageAPI.class);
		Call<List<Candidate>> apiCall = searchByFaceImageAPI.searchByFaceImage(workflowId, workflowTcn,
		                                                                       facePhotoBase64);
		TaskResponse<List<Candidate>> taskResponse = Context.getWebserviceManager().executeApi(apiCall);

		boolean notMatch = !taskResponse.isSuccess() && "B003-00002".equals(taskResponse.getErrorCode());

		if (notMatch) { return; }

		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		candidates = taskResponse.getResult();
	}
}