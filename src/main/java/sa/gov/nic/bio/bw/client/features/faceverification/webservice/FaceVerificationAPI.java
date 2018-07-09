package sa.gov.nic.bio.bw.client.features.faceverification.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;

public interface FaceVerificationAPI
{
	@FormUrlEncoded
	@POST
	Call<PersonInfo> verifyFaceImage(@Url String url, @Field("person-id") long personId,
	                                                  @Field("image") String imageBase64);
}