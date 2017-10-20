package sa.gov.nic.bio.bw.client.cancelcriminal.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface CancelCriminalAPI
{
	@FormUrlEncoded
	@POST
	Call<Boolean> cancelCriminal(@Url String url, @Field("person-id") String personId, @Field("criminal-id") String criminalId);
}