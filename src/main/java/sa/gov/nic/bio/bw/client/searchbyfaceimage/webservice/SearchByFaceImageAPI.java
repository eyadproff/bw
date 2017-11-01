package sa.gov.nic.bio.bw.client.searchbyfaceimage.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

import java.util.List;

public interface SearchByFaceImageAPI
{
	@FormUrlEncoded
	@POST
	Call<List<Candidate>> searchByFaceImage(@Url String url, @Field("image") String imageBase64);
}