package sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHit;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitProcessingStatus;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.webservice.LatentAPI;

import java.util.List;

public class LatentHitsInquiryBySearchCriteriaWorkflowTask extends WorkflowTask
{
	@Input private Long transactionNumber;
	@Input private Long civilBiometricsId;
	@Input private Long personId;
	@Input private Long referenceNumber;
	@Input private Integer locationId;
	@Input private LatentHitProcessingStatus status;
	@Input private Long entryDateFrom;
	@Input private Long entryDateTo;
	@Input(alwaysRequired = true) private Integer recordsPerPage;
	@Input(alwaysRequired = true) private Integer pageIndex;
	@Output private List<LatentHit> latentHits;
	@Output private Integer resultsTotalCount;
	
	@Override
	public void execute() throws Signal
	{
		int start = recordsPerPage * pageIndex + 1;
		int end = start + recordsPerPage - 1;
		
		var api = Context.getWebserviceManager().getApi(LatentAPI.class);
		var call = api.inquireLatentHitsBySearchCriteria(workflowId, workflowTcn, transactionNumber, civilBiometricsId,
				                                      personId, referenceNumber, locationId, status,
				                                      entryDateFrom, entryDateTo, start, end);
		var taskResponse = Context.getWebserviceManager().executeApi(call);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		var searchQueryResult = taskResponse.getResult();
		latentHits = searchQueryResult.getList();
		resultsTotalCount = searchQueryResult.getTotal();
	}
}