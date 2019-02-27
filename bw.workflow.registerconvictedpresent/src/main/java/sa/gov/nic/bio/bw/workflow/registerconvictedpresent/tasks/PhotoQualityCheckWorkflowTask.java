package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.face.beans.GetIcaoImageResponse;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.utils.RegisterConvictedPresentErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PhotoQualityCheckWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private String photoBase64;
	@Output private GetIcaoImageResponse result;
	
	@Override
	public void execute() throws Signal
	{
		Future<TaskResponse<GetIcaoImageResponse>> future = Context.getBioKitManager().getFaceUtilitiesService()
																					  .getIcaoImage(photoBase64);
		
		TaskResponse<GetIcaoImageResponse> response;
		try
		{
			response = future.get();
		}
		catch(Exception e)
		{
			if(e instanceof ExecutionException && e.getCause() instanceof NotConnectedException)
			{
				String errorCode = RegisterConvictedPresentErrorCodes.C007_00008.getCode();
				resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode));
				return;
			}
			
			String errorCode = RegisterConvictedPresentErrorCodes.C007_00009.getCode();
			String[] errorDetails = {"Failed to call the service for photo quality check!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
			return;
		}
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(response);
		result = response.getResult();
	}
}