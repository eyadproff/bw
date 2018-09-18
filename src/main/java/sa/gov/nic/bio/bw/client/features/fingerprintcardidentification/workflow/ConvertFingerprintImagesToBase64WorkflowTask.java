package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConvertFingerprintImagesToBase64WorkflowTask implements WorkflowTask
{
	@Input(required = true) private Map<Integer, Image> fingerprintImages;
	@Output private Map<Integer, String> fingerprintBase64Images;
	
	@Override
	public ServiceResponse<?> execute()
	{
		fingerprintBase64Images = new HashMap<>();
		
		for(Entry<Integer, Image> entry : fingerprintImages.entrySet())
		{
			try
			{
				fingerprintBase64Images.put(entry.getKey(), AppUtils.imageToBase64(entry.getValue()));
			}
			catch(IOException e)
			{
				String errorCode = FingerprintCardIdentificationErrorCodes.C013_00002.getCode();
				String[] errorDetails = {"failed to convert images to base64 string!"};
				return ServiceResponse.failure(errorCode, e, errorDetails);
			}
		}
		
		return ServiceResponse.success();
	}
}