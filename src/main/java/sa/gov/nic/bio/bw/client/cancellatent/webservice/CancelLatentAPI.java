package sa.gov.nic.bio.bw.client.cancellatent.webservice;

import retrofit2.Call;
import retrofit2.http.POST;

public interface CancelLatentAPI
{
	@POST("services-gateway-cancellatent/api/latent/cancellatenthit/v1")
	Call<Boolean> cancelLatent();
}