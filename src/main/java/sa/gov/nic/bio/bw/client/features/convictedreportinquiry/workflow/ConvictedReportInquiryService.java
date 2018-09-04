package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.workflow;

import retrofit2.Call;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.webservice.ConvictedReportInquiryAPI;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;

public class ConvictedReportInquiryService
{
	public static ServiceResponse<List<ConvictedReport>> execute(long generalFileNumber)
	{
		ConvictedReportInquiryAPI convictedReportInquiryAPI =
												Context.getWebserviceManager().getApi(ConvictedReportInquiryAPI.class);
		Call<List<ConvictedReport>> apiCall =
								convictedReportInquiryAPI.inquireConvictedReportByGeneralFileNumber(generalFileNumber);
		return Context.getWebserviceManager().executeApi(apiCall);
	}
}