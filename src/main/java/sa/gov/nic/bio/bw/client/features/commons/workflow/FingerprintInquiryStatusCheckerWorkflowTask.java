package sa.gov.nic.bio.bw.client.features.commons.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.bw.client.features.commons.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.commons.TaskResponse;

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
	public void execute() throws Signal
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
				Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		Call<FingerprintInquiryStatusResult> apiCall = fingerprintInquiryAPI.checkFingerprintsInquiryStatus(inquiryId);
		TaskResponse<FingerprintInquiryStatusResult> taskResponse =
																	Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		FingerprintInquiryStatusResult result = taskResponse.getResult();
		if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_PENDING)
		{
			status = Status.PENDING;
		}
		else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_NO_HIT)
		{
			status = Status.NOT_HIT;
		}
		else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_HIT)
		{
			status = Status.HIT;
			samisId = result.getSamisId();
			civilBioId = result.getCivilHitBioId();
			criminalBioId = result.getCrimnalHitBioId();
			personInfo = result.getPersonInfo();
		}
		else
		{
			String errorCode = CommonsErrorCodes.C008_00020.getCode();
			String[] errorDetails = {"The returned status for the fingerprint inquiry is unknown (" +
					result.getStatus() + ")!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, null, errorDetails));
		}
	}
}