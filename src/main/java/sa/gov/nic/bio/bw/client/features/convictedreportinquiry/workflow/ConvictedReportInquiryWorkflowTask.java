package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class ConvictedReportInquiryWorkflowTask implements WorkflowTask
{
	@Input(required = true) private long generalFileNumber;
	@Output private List<ConvictedReport> convictedReports;
	
	@Override
	public ServiceResponse<?> execute()
	{
		ConvictedReportInquiryAPI convictedReportInquiryAPI =
												Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<List<ConvictedReport>> apiCall =
								convictedReportInquiryAPI.inquireConvictedReportByGeneralFileNumber(generalFileNumber);
		ServiceResponse<List<ConvictedReport>> serviceResponse = Context.getWebserviceManager().executeApi(apiCall);
		
		if(serviceResponse.isSuccess()) convictedReports = serviceResponse.getResult();
		
		return serviceResponse;
	}
}