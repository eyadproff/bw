package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

public class CheckCitizenRegistrationWorkflowTask extends WorkflowTask
{
	public enum Status
	{
		PENDING,
		SUCCESS
	}
	
//	@Input(alwaysRequired = true) private Long tcn;
	@Output private Status status;

	public  static  int x=3;
	
	@Override
	public void execute() throws Signal
	{
//		var api = Context.getWebserviceManager().getApi(CitizenEnrollmentAPI.class);
//		var apiCall = api.checkCitizenRegistration(workflowId, workflowTcn, tcn);
//		var taskResponse = Context.getWebserviceManager().executeApi(apiCall);
//		resetWorkflowStepIfNegativeTaskResponse(taskResponse);
//
//		Integer httpCode = taskResponse.getHttpCode();
//		if(httpCode == 200) status = Status.SUCCESS;
//		else if(httpCode == 202) status = Status.PENDING;

		if(x-->0)status = Status.PENDING;
		else status = Status.SUCCESS;

		System.out.println(status);
	}
}