package sa.gov.nic.bio.bw.workflow.deleteconvictedreport.utils;

public enum DeleteConvictedReportErrorCodes
{
	C014_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}