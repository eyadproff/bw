package sa.gov.nic.bio.bw.home.utils;

public enum HomeErrorCodes
{
	C004_00001, C004_00002, C004_00003,
	
	N004_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}