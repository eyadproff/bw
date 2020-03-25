package sa.gov.nic.bio.bw.workflow.commons.tasks;

import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintWsqResponse;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ConvertWsqToFingerprintBase64ImagesWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintWsqImages;
	@Output private Map<Integer, String> fingerprintBase64Images;
	
	@Override
	public void execute() throws Signal
	{
		var serviceResponseFuture = Context.getBioKitManager().getFingerprintUtilitiesService().convertWsqToImages(fingerprintWsqImages);
		
		TaskResponse<ConvertedFingerprintImagesResponse> response;
		try
		{
			response = serviceResponseFuture.get();
		}
		catch(Exception e)
		{
			if(e instanceof ExecutionException && e.getCause() instanceof NotConnectedException)
			{
				String errorCode = CommonsErrorCodes.N008_00001.getCode();
				resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode));
				return;
			}
			
			String errorCode = CommonsErrorCodes.C008_00058.getCode();
			String[] errorDetails = {"Failed to call the service for converting from WSQ!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
			return;
		}
		
		ConvertedFingerprintImagesResponse result = response.getResult();
		
		if(result.getReturnCode() == ConvertedFingerprintWsqResponse.SuccessCodes.SUCCESS)
		{
			fingerprintBase64Images = result.getFingerprintImagesMap();
		}
		else
		{
			String errorCode = CommonsErrorCodes.C008_00059.getCode();
			String[] errorDetails = {"ConvertedFingerprintImagesResponse.FailureCodes.UNKNOWN (" + result.getReturnCode() + ")"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
		}
	}

	public void setFingerprintWsqImages(Map<Integer, String> fingerprintWsqImages)
	{
		this.fingerprintWsqImages = fingerprintWsqImages;
	}
	
	public Map<Integer, String> getFingerprintBase64Images()
	{
		return fingerprintBase64Images;
	}
}