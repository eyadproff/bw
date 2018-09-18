package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class FingerprintInquiryWorkflowTask implements WorkflowTask
{
	@Input(required = true) private List<Finger> fingerprints;
	@Input(required = true) private List<Integer> missingFingerprints;
	@Output private Integer inquiryId;
	
	@Override
	public ServiceResponse<?> execute()
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
				Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		
		String a = new Gson().toJson(fingerprints,
		                             TypeToken.getParameterized(List.class, Finger.class).getType());
		String b = new Gson().toJson(missingFingerprints,
		                             TypeToken.getParameterized(List.class, Integer.class).getType());
		
		Call<Integer> apiCall = fingerprintInquiryAPI.inquireWithFingerprints(a, b);
		ServiceResponse<Integer> serviceResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		if(serviceResponse.isSuccess()) inquiryId = serviceResponse.getResult();
		
		return serviceResponse;
	}
}