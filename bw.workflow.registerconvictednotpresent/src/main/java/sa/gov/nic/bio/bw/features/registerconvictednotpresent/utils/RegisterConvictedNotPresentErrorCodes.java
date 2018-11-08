package sa.gov.nic.bio.bw.features.registerconvictednotpresent.utils;

public enum RegisterConvictedNotPresentErrorCodes
{
	C009_00001, C009_00002, C009_00003, C009_00004;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}