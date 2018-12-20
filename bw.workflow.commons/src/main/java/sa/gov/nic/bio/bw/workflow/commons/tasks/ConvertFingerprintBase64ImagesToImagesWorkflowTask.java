package sa.gov.nic.bio.bw.workflow.commons.tasks;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConvertFingerprintBase64ImagesToImagesWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
	@Output private Map<Integer, Image> fingerprintImages;
	
	@Override
	public void execute()
	{
		fingerprintImages = new HashMap<>();
		
		for(Entry<Integer, String> entry : fingerprintBase64Images.entrySet())
		{
			String fingerprintBase64Image = entry.getValue();
			if(fingerprintBase64Image != null) fingerprintImages.put(entry.getKey(),
			                                                         AppUtils.imageFromBase64(fingerprintBase64Image));
		}
	}
}