package sa.gov.nic.bio.bw.features.cancelcriminal.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.features.cancelcriminal.controllers.CancelCriminalPaneFxController.CancelCriminalMethod;
import sa.gov.nic.bio.bw.features.cancelcriminal.webservice.CancelCriminalAPI;
import sa.gov.nic.bio.commons.TaskResponse;

public class CancelCriminalWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private CancelCriminalMethod cancelCriminalMethod;
	@Input(alwaysRequired = true) private long criminalId;
	@Input(requiredOnlyIf = {"cancelCriminalMethod=BY_INQUIRY_ID"}) private Long inquiryId;
	@Input(requiredOnlyIf = {"cancelCriminalMethod=BY_PERSON_ID"}) private Long personId;
	@Input(requiredOnlyIf = {"cancelCriminalMethod=BY_PERSON_ID"}) private Integer samisIdType;
	@Output private Boolean success;
	
	@Override
	public void execute() throws Signal
	{
		CancelCriminalAPI cancelCriminalAPI = Context.getWebserviceManager().getApi(CancelCriminalAPI.class);
		Call<Boolean> apiCall;
		
		switch(cancelCriminalMethod)
		{
			default:
			case BY_PERSON_ID:
			{
				apiCall = cancelCriminalAPI.cancelCriminalByPersonId(personId, samisIdType, criminalId);
				break;
			}
			case BY_INQUIRY_ID:
			{
				apiCall = cancelCriminalAPI.cancelCriminalByInquiryId(inquiryId, criminalId);
				break;
			}
		}
		
		TaskResponse<Boolean> taskResponse = Context.getWebserviceManager().executeApi(apiCall);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		success = taskResponse.getResult();
	}
}