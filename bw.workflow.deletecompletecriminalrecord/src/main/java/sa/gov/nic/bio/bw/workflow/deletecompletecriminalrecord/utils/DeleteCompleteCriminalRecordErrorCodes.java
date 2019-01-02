package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.utils;

public enum DeleteCompleteCriminalRecordErrorCodes
{
	C015_00001,
	
	N015_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}