package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.tasks;

import javafx.concurrent.Task;
import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class InquiryTask extends Task<ServiceResponse<List<ConvictedReport>>>
{
	private long generalFileNumber;
	
	public InquiryTask(long generalFileNumber)
	{
		this.generalFileNumber = generalFileNumber;
	}
	
	@Override
	protected ServiceResponse<List<ConvictedReport>> call()
	{
		ConvictedReportInquiryAPI api = Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<List<ConvictedReport>> apiCall = api.inquireConvictedReportByGeneralFileNumber(generalFileNumber);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}