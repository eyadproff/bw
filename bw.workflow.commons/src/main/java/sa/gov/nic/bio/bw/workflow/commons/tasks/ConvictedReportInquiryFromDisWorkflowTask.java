package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.DisCriminalReport;
import sa.gov.nic.bio.bw.workflow.commons.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class ConvictedReportInquiryFromDisWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private long criminalBiometricsId;
	@Input private Boolean returnNullResultInCaseNotFound;
	@Output private List<DisCriminalReport> disCriminalReports;
	
	@Override
	public void execute() throws Signal
	{
		ConvictedReportInquiryAPI api = Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<List<DisCriminalReport>> call = api.inquireConvictedReportFromDisByGeneralFileNumber(workflowId,
		                                                                                          workflowTcn,
		                                                                                          criminalBiometricsId);
		TaskResponse<List<DisCriminalReport>> taskResponse = Context.getWebserviceManager().executeApi(call);
		
		
		boolean notFound = !taskResponse.isSuccess() && "B003-0015".equals(taskResponse.getErrorCode());
		
		if(returnNullResultInCaseNotFound != null && returnNullResultInCaseNotFound && notFound) return;
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		disCriminalReports = taskResponse.getResult();
	}
}