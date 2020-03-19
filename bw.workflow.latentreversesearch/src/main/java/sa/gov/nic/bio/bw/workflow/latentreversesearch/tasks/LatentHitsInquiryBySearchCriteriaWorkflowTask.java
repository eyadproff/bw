package sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks;

import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHit;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitProcessingStatus;

import java.util.ArrayList;
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
		
		//var api = Context.getWebserviceManager().getApi(LatentAPI.class);
		//var call = api.inquireLatentHitsBySearchCriteria(workflowId, workflowTcn, transactionNumber, civilBiometricsId,
		//		                                      personId, referenceNumber, locationId, status,
		//		                                      entryDateFrom, entryDateTo, start, end);
		//var taskResponse = Context.getWebserviceManager().executeApi(call);
		//
		//resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		//var searchQueryResult = taskResponse.getResult();
		//latentHits = searchQueryResult.getList();
		//resultsTotalCount = searchQueryResult.getTotal();
		
		// TODO: TEMP
		LatentHit latentHit = new LatentHit();
		latentHit.setTransactionNumber(1);
		latentHit.setCivilBiometricsId(2);
		latentHit.setPersonId(3);
		latentHit.setReferenceNumber(4);
		latentHit.setLocationId(5);
		latentHit.setStatus(LatentHitProcessingStatus.NEW);
		latentHit.setEntryDateTime(555);
		
		latentHits = new ArrayList<>();
		latentHits.add(latentHit);
		resultsTotalCount = 1;
	}
}