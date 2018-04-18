package sa.gov.nic.bio.bw.client.features.printconvictednotpresent.utils;

public enum PrintConvictedNotPresentErrorCodes
{
	C009_00001, C009_00002;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}