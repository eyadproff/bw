package sa.gov.nic.bio.bw.workflow.irisinquiry.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
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
	
	@Input(alwaysRequired = true) private Long tcn;
	@Output private Status status;
	@Output private Long civilBiometricsId;
	@Output private List<Long> civilPersonIds;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(IrisInquiryAPI.class);
		var apiCall = api.checkIrisInquiryStatus(workflowId, workflowTcn, tcn);
		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		if("B003-00068".equals(taskResponse.getErrorCode()))
		{
			status = Status.NOT_HIT;
			return;
		}
		else resetWorkflowStepIfNegativeTaskResponse(taskResponse);
		
		Integer httpCode = taskResponse.getHttpCode();
		if(httpCode == 200)
		{
			status = Status.HIT;
			var result = taskResponse.getResult();
			civilBiometricsId = result.getBioId();
			civilPersonIds = result.getSamisIdList();
		}
		else if(httpCode == 202) status = Status.PENDING;
	}
}