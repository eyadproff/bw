package sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.utils;

public enum RegisterConvictedNotPresentErrorCodes
{
	C009_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}