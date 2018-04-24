package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.utils;

public enum RegisterConvictedNotPresentErrorCodes
{
	C009_00001, C009_00002;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}