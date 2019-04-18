package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.SearchQueryResult;
import sa.gov.nic.bio.bw.workflow.commons.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class ConvictedReportInquiryBySearchCriteriaWorkflowTask extends WorkflowTask
{
	@Input private Long reportNumber;
	@Input private Long criminalBiometricsId;
	@Input private Long location;
	@Input private Long personId;
	@Input private String documentId;
	@Input private String firstName;
	@Input private String fatherName;
	@Input private String grandfatherName;
	@Input private String familyName;
	@Input private String judgementNumber;
	@Input private Long prisonerNumber;
	@Input private Long operatorId;
	@Input private Long judgmentDateFrom;
	@Input private Long judgmentDateTo;
	@Input private Long rootReportNumber;
	@Input private Boolean showOldReports;
	@Input private Boolean showDeletedReports;
	@Input(alwaysRequired = true) private Integer recordsPerPage;
	@Input(alwaysRequired = true) private Integer pageIndex;
	@Output private List<ConvictedReport> convictedReports;
	@Output private Integer resultsTotalCount;
	
	@Override
	public void execute() throws Signal
	{
		int start = recordsPerPage * pageIndex + 1;
		int end = start + recordsPerPage - 1;
		
		ConvictedReportInquiryAPI api = Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<SearchQueryResult<ConvictedReport>> call =
				api.inquireConvictedReportBySearchCriteria(workflowId, workflowTcn, reportNumber, criminalBiometricsId,
				                                           location, judgementNumber, personId, prisonerNumber,
				                                           documentId, judgmentDateFrom, judgmentDateTo, firstName,
				                                           fatherName, grandfatherName, familyName, operatorId,
				                                           rootReportNumber, showOldReports, showDeletedReports, start,
				                                           end);
		TaskResponse<SearchQueryResult<ConvictedReport>> taskResponse = Context.getWebserviceManager().executeApi(call);
		
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		SearchQueryResult<ConvictedReport> searchQueryResult = taskResponse.getResult();
		convictedReports = searchQueryResult.getList();
		resultsTotalCount = searchQueryResult.getTotal();
	}
}