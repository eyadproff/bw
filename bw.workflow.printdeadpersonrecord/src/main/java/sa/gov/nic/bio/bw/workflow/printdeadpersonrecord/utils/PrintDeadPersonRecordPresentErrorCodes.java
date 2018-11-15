package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.utils;

public enum PrintDeadPersonRecordPresentErrorCodes
{
	C012_00001, C012_00002, C012_00003, C012_00004, C012_00005, C012_00006, C012_00007, C012_00008;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}