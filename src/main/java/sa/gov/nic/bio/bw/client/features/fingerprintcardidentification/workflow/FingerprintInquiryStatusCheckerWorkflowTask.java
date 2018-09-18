package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class FingerprintInquiryStatusCheckerWorkflowTask implements WorkflowTask
{
	public enum Status
	{
		HIT,
		NOT_HIT,
		PENDING
	}
	
	@Input(required = true) private Integer inquiryId;
	@Output private Status status;
	@Output private Long samisId;
	@Output private Long civilBioId;
	@Output private Long criminalBioId;
	@Output private PersonInfo personInfo;
	
	@Override
	public ServiceResponse<?> execute()
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
				Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		Call<FingerprintInquiryStatusResult> apiCall = fingerprintInquiryAPI.checkFingerprintsInquiryStatus(inquiryId);
		ServiceResponse<FingerprintInquiryStatusResult> serviceResponse =
																	Context.getWebserviceManager().executeApi(apiCall);
		
		if(!serviceResponse.isSuccess()) return serviceResponse;
		else
		{
			FingerprintInquiryStatusResult result = serviceResponse.getResult();
			if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_PENDING)
			{
				status = Status.PENDING;
				return ServiceResponse.success();
			}
			else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_NO_HIT)
			{
				status = Status.NOT_HIT;
				return ServiceResponse.success();
			}
			else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_HIT)
			{
				status = Status.HIT;
				samisId = result.getSamisId();
				civilBioId = result.getCivilHitBioId();
				criminalBioId = result.getCrimnalHitBioId();
				personInfo = result.getPersonInfo();
				return ServiceResponse.success();
			}
			else
			{
				String errorCode = FingerprintCardIdentificationErrorCodes.C013_00010.getCode();
				String[] errorDetails = {"The returned status for the fingerprint inquiry is unknown (" +
																						result.getStatus() + ")!"};
				return ServiceResponse.failure(errorCode, null, errorDetails);
			}
		}
	}
}