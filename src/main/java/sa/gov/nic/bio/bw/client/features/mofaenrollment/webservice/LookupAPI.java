package sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface LookupAPI
{
	@GET
	Call<List<NationalityBean>> lookupNationalities(@Url String url);
	
	@GET
	Call<List<VisaTypeBean>> lookupVisaTypes(@Url String url);
}