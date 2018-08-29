package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.util.List;

public interface SearchByFaceImageAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/face/search/v1")
	Call<List<Candidate>> searchByFaceImage(@Field("image") String imageBase64);
}