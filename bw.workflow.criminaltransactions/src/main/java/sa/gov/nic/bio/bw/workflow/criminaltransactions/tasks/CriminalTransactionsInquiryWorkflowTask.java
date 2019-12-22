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
	@Output private List<CriminalTransaction> criminalTransactions;
	@Output private Integer resultsTotalCount;
	
	@Override
	public void execute() throws Signal
	{
		var api = Context.getWebserviceManager().getApi(CriminalTransactionsAPI.class);
		var call = api.inquireCriminalTransactions(workflowId, workflowTcn, criminalBiometricsId);
		var taskResponse = Context.getWebserviceManager().executeApi(call);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		var result = taskResponse.getResult();
		criminalTransactions = result.getList();
		resultsTotalCount = result.getTotal();
	}
}