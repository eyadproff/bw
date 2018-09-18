package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintWsqResponse;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.Future;

public class ConvertFingerprintBase64ImagesToWsqWorkflowTask implements WorkflowTask
{
	@Input(required = true) private Map<Integer, String> fingerprintBase64Images;
	@Output private Map<Integer, String> fingerprintWsqImages;
	
	@Override
	public ServiceResponse<?> execute()
	{
		Future<sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintWsqResponse>>
										 serviceResponseFuture = Context.getBioKitManager()
																		.getFingerprintUtilitiesService()
																		.convertImagesToWsq(fingerprintBase64Images);
		
		sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintWsqResponse> response;
		try
		{
			response = serviceResponseFuture.get();
		}
		catch(Exception e)
		{
			String errorCode = FingerprintCardIdentificationErrorCodes.C013_00003.getCode();
			String[] errorDetails = {"Failed to call the service for converting to WSQ!"};
			return ServiceResponse.failure(errorCode, e, errorDetails);
		}
		
		if(response.isSuccess())
		{
			ConvertedFingerprintWsqResponse responseResult = response.getResult();
			fingerprintWsqImages = responseResult.getFingerprintWsqMap();
		}
		else
		{
			String[] errorDetails = {"Failed to convert to WSQ!"};
			return ServiceResponse.failure(response.getErrorCode(), response.getException(), errorDetails);
		}
		
		return ServiceResponse.success();
	}
}