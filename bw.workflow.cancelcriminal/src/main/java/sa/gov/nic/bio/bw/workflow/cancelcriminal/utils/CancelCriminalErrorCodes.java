package sa.gov.nic.bio.bw.workflow.cancelcriminal.utils;

public enum CancelCriminalErrorCodes
{
	;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}