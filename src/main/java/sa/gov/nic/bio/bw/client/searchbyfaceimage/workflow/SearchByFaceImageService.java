package sa.gov.nic.bio.bw.client.searchbyfaceimage.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.cancelcriminal.webservice.CancelCriminalAPI;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponse;
import sa.gov.nic.bio.bw.client.core.workflow.ServiceBase;
import sa.gov.nic.bio.bw.client.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.searchbyfaceimage.webservice.SearchByFaceImageAPI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
			// TODO: handle the exception
			e.printStackTrace();
		}
		
		SearchByFaceImageAPI searchByFaceImageAPI = Context.getWebserviceManager().getApi(SearchByFaceImageAPI.class);
		String url = System.getProperty("jnlp.bw.service.searchByFaceImage");
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
					String photoPath = savePhoto(candidateImage);
					candidate.setImage(null);
					candidate.setPhotoPath(photoPath);
				});
			}
		}
		
		bypassResponse(execution, response, true);
	}
	
	private static String savePhoto(String photoBase64) // TODO: temp
	{
		byte[] photoByteArray = Base64.getDecoder().decode(photoBase64);
		InputStream is = new ByteArrayInputStream(photoByteArray);
		String fileName = System.nanoTime() + ".jpg";
		String filePath = AppConstants.TEMP_FOLDER_PATH + "/" + fileName;
		
		try
		{
			Files.copy(is, Paths.get(filePath));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return filePath;
	}
}