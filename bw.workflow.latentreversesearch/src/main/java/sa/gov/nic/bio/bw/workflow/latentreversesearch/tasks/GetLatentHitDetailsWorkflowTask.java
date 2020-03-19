package sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks;

import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.Latent;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitProcessingStatus;

import java.util.ArrayList;
import java.util.List;

public class GetLatentHitDetailsWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Long transactionNumber;
	@Output private LatentHitDetails latentHitDetails;
	
	@Override
	public void execute() throws Signal
	{
		//var api = Context.getWebserviceManager().getApi(LatentAPI.class);
		//var call = api.getLatentHitDetails(workflowId, workflowTcn, transactionNumber);
		//var taskResponse = Context.getWebserviceManager().executeApi(call);
		//
		//resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		//latentHitDetails = taskResponse.getResult();
		
		// TODO: TEMP
		Latent latent = new Latent();
		latent.setNumber("111");
		latent.setScore(50);
		
		List<Latent> latents = new ArrayList<>();
		latents.add(latent);
		
		latentHitDetails = new LatentHitDetails();
		latentHitDetails.setLatents(latents);
		latentHitDetails.setStatus(LatentHitProcessingStatus.IN_PROGRESS);
	}
}