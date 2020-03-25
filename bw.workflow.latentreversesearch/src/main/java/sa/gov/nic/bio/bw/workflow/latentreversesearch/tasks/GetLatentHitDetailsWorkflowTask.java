package sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJobDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice.LatentAPI;

import java.util.HashMap;
import java.util.Map;

public class GetLatentHitDetailsWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long tcn;
	@Output private LatentJobDetails latentJobDetails;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(LatentAPI.class);
		var call = api.getLatentJobDetails(workflowId, workflowTcn, tcn);
		var taskResponse = Context.getWebserviceManager().executeApi(call);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		latentJobDetails = taskResponse.getResult();
		
		var latentHitsDetails = latentJobDetails.getLatentHitsDetails();
		if(latentHitsDetails != null)
		{
			var latentHitDetails = latentHitsDetails.getLatentHitDetails();
			if(latentHitDetails != null)
			{
				Map<Long, String> latentImagesBase64 = new HashMap<>();
				
				for(long latentId : latentHitDetails.keySet())
				{
					var call2 = api.getLatentInfo(workflowId, workflowTcn, latentId);
					var taskResponse2 = Context.getWebserviceManager().executeApi(call2);
					resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse2);
					var latentInfo = taskResponse2.getResult();
					latentImagesBase64.put(latentId, latentInfo.getFingerImage());
				}
				
				latentHitsDetails.setLatentImagesBase64(latentImagesBase64);
			}
		}
	}
}