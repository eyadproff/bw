package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.SearchByFaceImageAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class SearchByFaceImageService
{
	public static ServiceResponse<List<Candidate>> execute(String imageBase64)
	{
		SearchByFaceImageAPI searchByFaceImageAPI = Context.getWebserviceManager().getApi(SearchByFaceImageAPI.class);
		Call<List<Candidate>> apiCall = searchByFaceImageAPI.searchByFaceImage(imageBase64);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}