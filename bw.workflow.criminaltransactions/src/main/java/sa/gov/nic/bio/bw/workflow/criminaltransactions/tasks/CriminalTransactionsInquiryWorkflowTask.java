package sa.gov.nic.bio.bw.workflow.criminaltransactions.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.beans.CriminalTransaction;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.webservice.CriminalTransactionsAPI;

import java.util.List;

public class CriminalTransactionsInquiryWorkflowTask extends WorkflowTask
{
	@Input private Long criminalBiometricsId;
	@Input private Long reportNumber;
	@Input private Integer location;
	@Input private Long operatorId;
	@Input private Long criminalDelinkId;
	@Input private Integer transactionType;
	@Input(alwaysRequired = true) private Integer recordsPerPage;
	@Input(alwaysRequired = true) private Integer pageIndex;
	@Output private List<CriminalTransaction> criminalTransactions;
	@Output private Integer resultsTotalCount;
	
	@Override
	public void execute() throws Signal
	{
		int start = recordsPerPage * pageIndex + 1;
		int end = start + recordsPerPage - 1;
		
		var api = Context.getWebserviceManager().getApi(CriminalTransactionsAPI.class);
		var call = api.inquireCriminalTransactions(workflowId, workflowTcn, transactionType, criminalBiometricsId, reportNumber, criminalDelinkId, start, end, location, operatorId);
		var taskResponse = Context.getWebserviceManager().executeApi(call);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		var result = taskResponse.getResult();
		criminalTransactions = result.getList();
		resultsTotalCount = result.getTotal();
	}
}