package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.utils;

public enum RegisterConvictedPresentErrorCodes
{
	C007_00001, C007_00002, C007_00003;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}