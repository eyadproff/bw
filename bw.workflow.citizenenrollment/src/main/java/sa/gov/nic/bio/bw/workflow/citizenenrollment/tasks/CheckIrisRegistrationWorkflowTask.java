package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

public class CheckIrisRegistrationWorkflowTask extends WorkflowTask
{
	public enum Status
	{
		PENDING,
		SUCCESS
	}
	
//	@Input(alwaysRequired = true) private Long tcn;
public  static  int x=3;
	@Output private Status status;
	
	@Override
	public void execute() throws Signal
	{
//		var api = Context.getWebserviceManager().getApi(IrisRegistrationAPI.class);
//		var apiCall = api.checkIrisRegistration(workflowId, workflowTcn, tcn);
//		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
//		resetWorkflowStepIfNegativeTaskResponse(taskResponse);
//
//		Integer httpCode = taskResponse.getHttpCode();
//		if(httpCode == 200) status = Status.SUCCESS;
//		else if(httpCode == 202) status = Status.PENDING;




		if(x-->0)status = Status.PENDING;
		else status = Status.SUCCESS;

		System.out.println(status);
		//resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(CitizenEnrollmentErrorCodes.B018_00001.getCode()));
	}
}