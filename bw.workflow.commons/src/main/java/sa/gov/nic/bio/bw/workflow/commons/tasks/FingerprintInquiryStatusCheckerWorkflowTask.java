package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.bw.workflow.commons.webservice.FingerprintInquiryAPI;
import sa.gov.nic.bio.bw.workflow.commons.beans.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class FingerprintInquiryStatusCheckerWorkflowTask implements WorkflowTask
{
	public enum Status
	{
		HIT,
		NOT_HIT,
		PENDING
	}
	
	@Input(alwaysRequired = true) private Integer inquiryId;
	@Output private Status status;
	@Output private Long civilBiometricsId;
	@Output private Long criminalBiometricsId;
	@Output private List<Long> civilPersonIds;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn) throws Signal, InterruptedException
	{
		FingerprintInquiryAPI fingerprintInquiryAPI =
				Context.getWebserviceManager().getApi(FingerprintInquiryAPI.class);
		Call<FingerprintInquiryStatusResult> apiCall =
							fingerprintInquiryAPI.checkFingerprintsInquiryStatus(workflowId, workflowTcn, inquiryId);
		TaskResponse<FingerprintInquiryStatusResult> taskResponse = Context.getWebserviceManager()
																		.executeApi(apiCall);
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
			civilBiometricsId = result.getCivilHitBioId();
			criminalBiometricsId = result.getCrimnalHitBioId();
			civilPersonIds = result.getCivilIdList();
			
			if(civilBiometricsId != null && civilBiometricsId <= 0L) civilBiometricsId = null;
			if(criminalBiometricsId != null && criminalBiometricsId <= 0L) criminalBiometricsId = null;
		}
		else
		{
			String errorCode = CommonsErrorCodes.C008_00016.getCode();
			String[] errorDetails = {"The returned status for the fingerprint inquiry is unknown (" +
					result.getStatus() + ")!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, null, errorDetails));
		}
	}
}