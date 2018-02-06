package sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface CancelCriminalAPI
{
	@FormUrlEncoded
	@POST
	Call<Boolean> cancelCriminalByPersonId(@Url String url, @Field("person-id") long personId,
	                                       @Field("person-type") int personIdType,
	                                       @Field("criminal-id") long criminalId);
	
	@FormUrlEncoded
	@POST
	Call<Boolean> cancelCriminalByInquiryId(@Url String url, @Field("inquiry-id") long inquiryId,
	                                        @Field("criminal-id") long criminalId);
}