package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface LookupAPI
{
	@GET("services-gateway-lookups/api/application/visa-type/all/v2")
	Call<List<VisaTypeBean>> lookupVisaTypes();
	
	@GET("services-gateway-lookups/api/application/passport-types/v1")
	Call<List<PassportTypeBean>> lookupPassportTypes();
}