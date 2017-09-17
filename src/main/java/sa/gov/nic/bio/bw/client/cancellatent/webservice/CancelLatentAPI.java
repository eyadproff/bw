package sa.gov.nic.bio.bw.client.cancellatent.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CancelLatentAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/latent/delink/v1")
	Call<Boolean> cancelLatent(@Field("personId") String personId, @Field("latentRecord") String latentRecord);
}