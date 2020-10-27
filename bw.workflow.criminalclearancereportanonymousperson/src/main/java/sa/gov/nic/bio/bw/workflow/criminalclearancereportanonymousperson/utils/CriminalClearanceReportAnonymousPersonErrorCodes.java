package sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.utils;

public enum CriminalClearanceReportAnonymousPersonErrorCodes
{
	C019_00001, C019_00002, C019_00003, C019_00004,C019_00005, C019_00006, C019_00007, C019_00008;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}