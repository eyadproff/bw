package sa.gov.nic.bio.bw.features.cancelcriminal.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CancelCriminalAPI
{
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/delink/person-id/v1")
	Call<Boolean> cancelCriminalByPersonId(@Field("person-id") long personId, @Field("person-type") int samisIdTypes, @Field("criminal-id") long criminalId);
	
	@FormUrlEncoded
	@POST("services-gateway-biooperation/api/xafis/delink/inquiry-id/v1")
	Call<Boolean> cancelCriminalByInquiryId(@Field("inquiry-id") long inquiryId, @Field("criminal-id") long criminalId);
}