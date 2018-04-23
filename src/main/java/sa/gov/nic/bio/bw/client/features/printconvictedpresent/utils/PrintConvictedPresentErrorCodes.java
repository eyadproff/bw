package sa.gov.nic.bio.bw.client.features.printconvictedpresent.utils;

public enum PrintConvictedPresentErrorCodes
{
	C007_00001, C007_00002, C007_00003;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}