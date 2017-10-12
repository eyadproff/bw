package sa.gov.nic.bio.bw.client.cancelcriminal.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CancelCriminalAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/delink/v1")
	Call<Boolean> cancelCriminal(@Field("person-id") String personId, @Field("criminal-id") String criminalId);
}