package sa.gov.nic.bio.bw.workflow.commons.tasks;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConvertFingerprintBase64ImagesToImagesWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
	@Output private Map<Integer, Image> fingerprintImages;
	
	@Override
	public void execute() throws Signal
	{
		fingerprintBase64Images = new HashMap<>();
		
		for(Entry<Integer, Image> entry : fingerprintImages.entrySet())
		{
			try
			{
				Image image = entry.getValue();
				if(image != null) fingerprintBase64Images.put(entry.getKey(), AppUtils.imageToBase64(image));
			}
			catch(IOException e)
			{
				String errorCode = CommonsErrorCodes.C008_00032.getCode();
				String[] errorDetails = {"failed to convert images to base64 string!"};
				resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
			}
		}
	}
}