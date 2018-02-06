package sa.gov.nic.bio.bw.client.features.cancellatent.webservice;

import retrofit2.Call;
import retrofit2.http.*;

public interface CancelLatentAPI
{
	@FormUrlEncoded
	@POST
	Call<Boolean> cancelLatent(@Url String url, @Field("person-id") long personId, @Field("latent-id") String latentId);
}