package sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJob;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJobStatus;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice.LatentAPI;

import java.util.List;

public class LatentJobsInquiryBySearchCriteriaWorkflowTask extends WorkflowTask
{
	@Input private Long jobId;
	@Input private Long civilBiometricsId;
	@Input private Long personId;
	@Input private Long tcn;
	@Input private Integer locationId;
	@Input private LatentJobStatus status;
	@Input private Long createDateFrom;
	@Input private Long createDateTo;
	@Input(alwaysRequired = true) private Integer recordsPerPage;
	@Input(alwaysRequired = true) private Integer pageIndex;
	@Output private List<LatentJob> latentJobs;
	@Output private Integer resultsTotalCount;
	
	@Override
	public void execute() throws Signal
	{
		int start = recordsPerPage * pageIndex + 1;
		int end = start + recordsPerPage - 1;
		
		var api = Context.getWebserviceManager().getApi(LatentAPI.class);
		var call = api.inquireLatentJobsBySearchCriteria(workflowId, workflowTcn, jobId, civilBiometricsId,
		                                                 personId, tcn, locationId, status, createDateFrom, createDateTo, start, end);
		var taskResponse = Context.getWebserviceManager().executeApi(call);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		var searchQueryResult = taskResponse.getResult();
		latentJobs = searchQueryResult.getList();
		resultsTotalCount = searchQueryResult.getTotal();
	}
}