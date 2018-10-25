package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintWsqResponse;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Map;
import java.util.concurrent.Future;

public class ConvertFingerprintBase64ImagesToWsqWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
	@Output private Map<Integer, String> fingerprintWsqImages;
	
	@Override
	public void execute() throws Signal
	{
		Future<TaskResponse<ConvertedFingerprintWsqResponse>>
										 serviceResponseFuture = Context.getBioKitManager()
																		.getFingerprintUtilitiesService()
																		.convertImagesToWsq(fingerprintBase64Images);
		
		TaskResponse<ConvertedFingerprintWsqResponse> response;
		try
		{
			response = serviceResponseFuture.get();
		}
		catch(Exception e)
		{
			String errorCode = FingerprintCardIdentificationErrorCodes.C013_00003.getCode();
			String[] errorDetails = {"Failed to call the service for converting to WSQ!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
			return;
		}
		
		ConvertedFingerprintWsqResponse responseResult = response.getResult();
		fingerprintWsqImages = responseResult.getFingerprintWsqMap();
	}
}