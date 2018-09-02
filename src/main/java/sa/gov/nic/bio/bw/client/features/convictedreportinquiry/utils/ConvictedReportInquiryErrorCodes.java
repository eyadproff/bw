package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.utils;

public enum ConvictedReportInquiryErrorCodes
{
	C014_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}