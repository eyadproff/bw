package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.utils;

public enum DeleteCompleteCriminalRecordErrorCodes
{
	C015_00001, C015_00002;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}