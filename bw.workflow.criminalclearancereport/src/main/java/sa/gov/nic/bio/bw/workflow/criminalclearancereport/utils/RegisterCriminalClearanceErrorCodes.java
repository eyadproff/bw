package sa.gov.nic.bio.bw.workflow.criminalclearancereport.utils;

public enum RegisterCriminalClearanceErrorCodes
{
	C020_00001, C020_00002, C020_00003, C020_00004, C020_00005, C020_00006, C020_00020, C020_00008, C020_00009;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}