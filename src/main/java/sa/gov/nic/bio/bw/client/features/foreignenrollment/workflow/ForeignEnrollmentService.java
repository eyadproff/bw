package sa.gov.nic.bio.bw.client.features.foreignenrollment.workflow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.ForeignEnrollmentAPI;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.ForeignInfo;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class ForeignEnrollmentService
{
	public static ServiceResponse<Long> execute(ForeignInfo foreignInfo)
	{
		ForeignEnrollmentAPI foreignEnrollmentAPI = Context.getWebserviceManager().getApi(ForeignEnrollmentAPI.class);
		String url = System.getProperty("jnlp.bio.bw.service.foreignEnrollment");
		
		String foreignInfoJson = new Gson().toJson(foreignInfo, TypeToken.get(ForeignInfo.class).getType());
		
		Call<Long> apiCall = foreignEnrollmentAPI.enrollForeign(url, foreignInfoJson);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}