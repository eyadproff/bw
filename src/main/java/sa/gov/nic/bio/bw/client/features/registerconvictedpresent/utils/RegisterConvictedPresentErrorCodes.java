package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.utils;

public enum RegisterConvictedPresentErrorCodes
{
	C007_00001, C007_00002, C007_00003, C007_00004, C007_00005, C007_00006, C007_00007, C007_00008, C007_00009;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}