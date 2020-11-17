package sa.gov.nic.bio.bw.workflow.criminalclearancereport.utils;

public enum RegisterCriminalClearanceErrorCodes
{
	C020_00001, C020_00002, C020_00003, C020_00004, C020_00005, C020_00006, C020_00020, C020_00008, C020_00009,
	C020_00010, C020_00011, C020_00012, C020_00013, C020_00014, C020_00015, C020_00016, C020_00017, C020_00018;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}