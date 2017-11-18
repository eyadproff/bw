package sa.gov.nic.bio.bw.client.searchbyfaceimage.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.workflow.ServiceBase;
import sa.gov.nic.bio.bw.client.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.searchbyfaceimage.webservice.SearchByFaceImageAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchByFaceImageService extends ServiceBase
{
	private static final Logger LOGGER = Logger.getLogger(SearchByFaceImageService.class.getName());
	
	@Override
	public void execute(DelegateExecution execution)
	{
		String uploadedImagePath = (String) execution.getVariable("uploadedImagePath");
		execution.removeVariables();
		
		String imageBase64 = null;
		try
		{
			imageBase64 = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(uploadedImagePath)));
		}
		catch(IOException e)
		{
			LOGGER.log(Level.SEVERE, "Failed to convert the file (" + uploadedImagePath + ") to base64-encoded image!", e);
			String errorCode = "C005-00001";
			bypassErrorCode(execution, errorCode);
		}
		
		SearchByFaceImageAPI searchByFaceImageAPI = Context.getWebserviceManager().getApi(SearchByFaceImageAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.searchByFaceImage");
		Call<List<Candidate>> apiCall = searchByFaceImageAPI.searchByFaceImage(url, imageBase64);
		ApiResponse<List<Candidate>> response = Context.getWebserviceManager().executeApi(apiCall);
		
		if(response.isSuccess())
		{
			List<Candidate> candidates = response.getResult();
			
			if(candidates != null)
			{
				candidates.forEach(candidate ->
				{
					String candidateImage = candidate.getImage();
					String photoPath = AppUtils.saveBase64PhotoToTemp(candidateImage, ".jpg");
					candidate.setImage(null); // because Activiti cannot handle big data
					candidate.setPhotoPath(photoPath);
				});
			}
		}
		
		bypassResponse(execution, response, true);
	}
}