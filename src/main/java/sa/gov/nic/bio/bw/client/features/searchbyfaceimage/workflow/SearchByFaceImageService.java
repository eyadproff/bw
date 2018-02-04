package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.utils.SearchByFaceImageErrorCodes;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.SearchByFaceImageAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class SearchByFaceImageService
{
	public static ServiceResponse<List<Candidate>> execute(String uploadedImagePath)
	{
		String imageBase64;
		try
		{
			imageBase64 = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(uploadedImagePath)));
		}
		catch(IOException e)
		{
			String errorCode = SearchByFaceImageErrorCodes.C005_00001.getCode();
			String[] errorDetails = {"Failed to convert image file to Base64-encoded representation!",
									 "uploadedImagePath = " + uploadedImagePath};
			return ServiceResponse.failure(errorCode, e, errorDetails);
		}
		
		SearchByFaceImageAPI searchByFaceImageAPI = Context.getWebserviceManager().getApi(SearchByFaceImageAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.searchByFaceImage");
		Call<List<Candidate>> apiCall = searchByFaceImageAPI.searchByFaceImage(url, imageBase64);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}