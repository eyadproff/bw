package sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqToFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJobDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice.LatentAPI;

import java.util.HashMap;
import java.util.List;
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
			List<Finger> civilFingers = latentHitsDetails.getCivilFingers();
			if(civilFingers != null)
			{
				Map<Integer, String> fingerImagesWsq = new HashMap<>();
				for(Finger civilFinger : civilFingers)
				{
					fingerImagesWsq.put(civilFinger.getType(), civilFinger.getImage());
				}
				
				var task = new ConvertWsqToFingerprintBase64ImagesWorkflowTask();
				task.setFingerprintWsqImages(fingerImagesWsq);
				task.execute();
				
				Map<Integer, String> fingerprintBase64Images = task.getFingerprintBase64Images();
				latentHitsDetails.setFingerImagesBase64(fingerprintBase64Images);
			}
			
			var latentHitDetails = latentHitsDetails.getLatentHitDetails();
			if(latentHitDetails != null)
			{
				Map<Long, String> latentImagesWsq = new HashMap<>();
				Map<Long, String> latentImagesBase64 = new HashMap<>();
				
				for(long latentId : latentHitDetails.keySet())
				{
					var call2 = api.getLatentInfo(workflowId, workflowTcn, latentId);
					var taskResponse2 = Context.getWebserviceManager().executeApi(call2);
					resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse2);
					var latentInfo = taskResponse2.getResult();
					String fingerImage = latentInfo.getFingerImage();
					latentImagesWsq.put(latentId, fingerImage);
					
					if(fingerImage != null)
					{
						Map<Integer, String> temp = new HashMap<>();
						temp.put(1, fingerImage);
						
						var task = new ConvertWsqToFingerprintBase64ImagesWorkflowTask();
						task.setFingerprintWsqImages(temp);
						task.execute();
						
						Map<Integer, String> fingerprintBase64Images = task.getFingerprintBase64Images();
						String imageBase64 = fingerprintBase64Images.get(1);
						latentImagesBase64.put(latentId, imageBase64);
					}
				}
				
				latentHitsDetails.setLatentImagesWsq(latentImagesWsq);
				latentHitsDetails.setLatentImagesBase64(latentImagesBase64);
			}
		}
	}
}