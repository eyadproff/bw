package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.utils;

public enum ConvictedReportInquiryErrorCodes
{
	C014_00001, C014_00002;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}