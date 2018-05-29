package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface MofaLookupAPI
{
	@GET
	Call<List<VisaTypeBean>> lookupVisaTypes(@Url String url);
	
	@GET
	Call<List<PassportTypeBean>> lookupPassportTypes(@Url String url);
}