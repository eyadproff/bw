package sa.gov.nic.bio.bw.workflow.commons.tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.List;

public class FingerprintInquiryWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private List<Finger> fingerprints;
	@Input(alwaysRequired = true) private List<Integer> missingFingerprints;
	@Output private Integer inquiryId;
	
	@Override
	public void execute() throws Signal
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
				Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		
		boolean removeThumbSlap = fingerprints.stream()
		                                      .anyMatch(finger ->
		                                                finger.getType() >= FingerPosition.RIGHT_THUMB.getPosition() &&
		                                                finger.getType() <= FingerPosition.LEFT_LITTLE.getPosition());
		
		List<Finger> fingerprintList = new ArrayList<>();
		fingerprints.forEach(finger ->
		{
			if(removeThumbSlap && (finger.getType() == FingerPosition.RIGHT_THUMB_SLAP.getPosition() ||
								   finger.getType() == FingerPosition.LEFT_THUMB_SLAP.getPosition())) return;
			
			fingerprintList.add(finger);
		});
		
		String a = new Gson().toJson(fingerprintList,
		                             TypeToken.getParameterized(List.class, Finger.class).getType());
		String b = new Gson().toJson(missingFingerprints,
		                             TypeToken.getParameterized(List.class, Integer.class).getType());
		
		Call<Integer> apiCall = fingerprintInquiryAPI.inquireWithFingerprints(workflowId, workflowTcn, a, b);
		TaskResponse<Integer> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		inquiryId = taskResponse.getResult();
	}
}