package sa.gov.nic.bio.bw.workflow.irisinquiry.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.irisinquiry.beans.IrisInquiryStatusResult;
import sa.gov.nic.bio.bw.workflow.irisinquiry.webservice.IrisInquiryAPI;

import java.util.List;

public class IrisInquiryStatusCheckerWorkflowTask extends WorkflowTask
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
	@Output private List<Long> civilPersonIds;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(IrisInquiryAPI.class);
		var apiCall = api.checkIrisInquiryStatus(workflowId, workflowTcn, inquiryId);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		var result = taskResponse.getResult();
		if(result.getStatus() == IrisInquiryStatusResult.STATUS_INQUIRY_PENDING)
		{
			status = Status.PENDING;
		}
		else if(result.getStatus() == IrisInquiryStatusResult.STATUS_INQUIRY_NO_HIT)
		{
			status = Status.NOT_HIT;
		}
		else if(result.getStatus() == IrisInquiryStatusResult.STATUS_INQUIRY_HIT)
		{
			status = Status.HIT;
			civilBiometricsId = result.getCivilHitBioId();
			civilPersonIds = result.getCivilIdList();
			
			if(civilBiometricsId != null && civilBiometricsId <= 0L) civilBiometricsId = null;
		}
	}
}