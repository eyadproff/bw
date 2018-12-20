package sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.utils;

public enum ConvictedReportInquiryErrorCodes
{
	C014_00001, C014_00002, C014_00003, C014_00004, C014_00005, C014_00006, C014_00007, C014_00008, C014_00009;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}