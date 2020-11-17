package sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.utils;

public enum CriminalClearanceReportAnonymousPersonErrorCodes
{
	C021_00001, C021_00002, C021_00003, C021_00004,C021_00005, C021_00006, C021_00007, C021_00008;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}