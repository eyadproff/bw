package sa.gov.nic.bio.bw.features.commons.workflow;

import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintWsqResponse;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.features.commons.utils.CommonsErrorCodes;
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
			String errorCode = CommonsErrorCodes.C008_00030.getCode();
			String[] errorDetails = {"Failed to call the service for converting to WSQ!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
			return;
		}
		
		ConvertedFingerprintWsqResponse result = response.getResult();
		
		if(result.getReturnCode() == ConvertedFingerprintWsqResponse.SuccessCodes.SUCCESS)
		{
			fingerprintWsqImages = result.getFingerprintWsqMap();
		}
		else
		{
			String errorCode = CommonsErrorCodes.C008_00031.getCode();
			String[] errorDetails = {"ConvertedFingerprintWsqResponse.FailureCodes.UNKNOWN (" +
																						result.getReturnCode() + ")"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
		}
	}
}